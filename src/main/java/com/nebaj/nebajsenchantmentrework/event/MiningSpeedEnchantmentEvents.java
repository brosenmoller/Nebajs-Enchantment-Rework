package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.attachment.ModAttachments;
import com.nebaj.nebajsenchantmentrework.attachment.MomentumState;
import com.nebaj.nebajsenchantmentrework.attachment.SurgingState;
import com.nebaj.nebajsenchantmentrework.component.ModDataComponents;
import com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments;
import com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments.SurgingStatus;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.mixin.ServerPlayerGameModeAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments.mainHandLevel;

public final class MiningSpeedEnchantmentEvents
{
    private static final Identifier MOMENTUM_MODIFIER_ID = NebajSEnchantmentRework.id("enchantment.momentum");
    private static final Identifier SURGING_MODIFIER_ID = NebajSEnchantmentRework.id("enchantment.surging");

    private MiningSpeedEnchantmentEvents() {}

    public static void initialize()
    {
        PlayerBlockBreakEvents.AFTER.register(MiningSpeedEnchantmentEvents::onBlockBroken);
        ServerTickEvents.END_SERVER_TICK.register(MiningSpeedEnchantmentEvents::tick);
    }

    private static void onBlockBroken(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity)
    {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer.isCreative()) return;

        long now = level.getGameTime();
        buildMomentum(serverPlayer, state, now);

        // Instamined blocks never set isDestroyingBlock, so without this they would surge for free
        if (state.getDestroySpeed(level, pos) <= 0) return;
        drainSurgingForBlock(serverPlayer, now);
    }

    private static void tick(MinecraftServer server)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            long now = player.level().getGameTime();
            boolean mining = ((ServerPlayerGameModeAccessor) player.gameMode).nebajsenchantmentrework$isDestroyingBlock();

            updateMomentum(player, now, mining);
            updateSurging(player, now, mining);
        }
    }

    private static void buildMomentum(ServerPlayer player, BlockState state, long now)
    {
        if (mainHandLevel(player, ModEnchantments.MOMENTUM) <= 0) return;

        MomentumState previous = player.getAttached(ModAttachments.MOMENTUM);
        int count = previous != null && previous.block() == state.getBlock() ? previous.count() + 1 : 1;

        player.setAttached(ModAttachments.MOMENTUM, new MomentumState(state.getBlock(), count));
        player.setAttached(ModAttachments.MOMENTUM_LAST_ACTIVE, now);
    }

    private static void updateMomentum(ServerPlayer player, long now, boolean mining)
    {
        int level = mainHandLevel(player, ModEnchantments.MOMENTUM);
        if (mining && level > 0) player.setAttached(ModAttachments.MOMENTUM_LAST_ACTIVE, now);

        Long lastActive = player.getAttached(ModAttachments.MOMENTUM_LAST_ACTIVE);
        if (lastActive != null && now - lastActive > MiningSpeedEnchantments.MOMENTUM_IDLE_TICKS)
        {
            player.removeAttached(ModAttachments.MOMENTUM_LAST_ACTIVE);
            player.removeAttached(ModAttachments.MOMENTUM);
        }

        MomentumState state = player.getAttached(ModAttachments.MOMENTUM);
        double multiplier = state == null ? 1.0 : MiningSpeedEnchantments.momentumMultiplier(state.count(), level);
        applyModifier(player, Attributes.BLOCK_BREAK_SPEED, MOMENTUM_MODIFIER_ID, multiplier - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void updateSurging(ServerPlayer player, long now, boolean mining)
    {
        int level = mainHandLevel(player, ModEnchantments.SURGING);
        if (level <= 0)
        {
            applyModifier(player, Attributes.MINING_EFFICIENCY, SURGING_MODIFIER_ID, 0.0, AttributeModifier.Operation.ADD_VALUE);
            return;
        }

        UUID pickaxeId = getOrAssignSurgingId(player.getMainHandItem());
        if (mining) mineWithSurging(player, pickaxeId, level, MiningSpeedEnchantments.SURGING_DRAIN_PER_TICK, now);

        SurgingStatus status = MiningSpeedEnchantments.surgingStatus(getSurgingStates(player).get(pickaxeId), now);
        double bonus = status.coolingDown() ? 0.0 : MiningSpeedEnchantments.surgingPeakBonus(level) * status.charge();
        applyModifier(player, Attributes.MINING_EFFICIENCY, SURGING_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void drainSurgingForBlock(ServerPlayer player, long now)
    {
        int level = mainHandLevel(player, ModEnchantments.SURGING);
        if (level <= 0) return;

        UUID pickaxeId = getOrAssignSurgingId(player.getMainHandItem());
        mineWithSurging(player, pickaxeId, level, MiningSpeedEnchantments.SURGING_DRAIN_PER_BLOCK, now);
    }

    // Mining while cooling down re-stamps the current charge, which pauses the refill until the player stops
    private static void mineWithSurging(ServerPlayer player, UUID pickaxeId, int level, float drain, long now)
    {
        Map<UUID, SurgingState> states = getSurgingStates(player);
        SurgingStatus status = MiningSpeedEnchantments.surgingStatus(states.get(pickaxeId), now);
        int recoveryTicks = MiningSpeedEnchantments.surgingRecoveryTicks(level);

        SurgingState next;
        if (status.coolingDown())
        {
            next = new SurgingState(status.charge(), true, now, recoveryTicks);
        }
        else
        {
            float drained = Math.max(0.0F, status.charge() - drain);
            next = new SurgingState(drained, drained <= 0.0F, now, recoveryTicks);
        }

        Map<UUID, SurgingState> updated = new HashMap<>();
        states.forEach((id, state) ->
        {
            if (MiningSpeedEnchantments.surgingStatus(state, now) != SurgingStatus.FULL) updated.put(id, state);
        });
        updated.put(pickaxeId, next);
        player.setAttached(ModAttachments.SURGING, Map.copyOf(updated));
    }

    private static Map<UUID, SurgingState> getSurgingStates(ServerPlayer player)
    {
        return player.getAttachedOrElse(ModAttachments.SURGING, Map.of());
    }

    private static UUID getOrAssignSurgingId(ItemStack stack)
    {
        UUID id = stack.get(ModDataComponents.SURGING_ID);
        if (id != null) return id;

        UUID assigned = UUID.randomUUID();
        stack.set(ModDataComponents.SURGING_ID, assigned);
        return assigned;
    }

    private static void applyModifier(ServerPlayer player, Holder<Attribute> attribute, Identifier modifierId, double amount, AttributeModifier.Operation operation)
    {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        AttributeModifier current = instance.getModifier(modifierId);
        if (amount <= 0)
        {
            if (current != null) instance.removeModifier(modifierId);
            return;
        }

        if (current != null && current.amount() == amount && current.operation() == operation) return;
        instance.addOrUpdateTransientModifier(new AttributeModifier(modifierId, amount, operation));
    }
}
