package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.config.ModConfig;
import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import com.nebaj.nebajsenchantmentrework.enchantment.DisabledEnchantments;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public final class WindBurstMaceEvents
{
    private static final float PROJECTILE_VELOCITY = 1.5f;
    private static final float PROJECTILE_INACCURACY = 1.0f;

    private WindBurstMaceEvents() {}

    public static void initialize()
    {
        UseItemCallback.EVENT.register(WindBurstMaceEvents::onUseItem);
    }

    private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand)
    {
        ModConfig.EnchantmentBehaviour behaviour = ModConfigManager.current().enchantmentBehaviour;
        ItemStack stack = player.getItemInHand(hand);
        if (!behaviour.maceWindBurst) return InteractionResult.PASS;
        if (DisabledEnchantments.isDisabled(Enchantments.WIND_BURST)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!hasWindBurst(level, stack)) return InteractionResult.PASS;
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel)
        {
            Projectile.spawnProjectileFromRotation(
                (projectileLevel, owner, weapon) -> new WindCharge(player, projectileLevel, player.getX(), player.getEyePosition().y(), player.getZ()),
                serverLevel,
                stack,
                player,
                0.0f,
                PROJECTILE_VELOCITY,
                PROJECTILE_INACCURACY
            );
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WIND_CHARGE_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
        }

        player.getCooldowns().addCooldown(stack, behaviour.maceWindBurstCooldownSeconds * SharedConstants.TICKS_PER_SECOND);
        return InteractionResult.SUCCESS;
    }

    private static boolean hasWindBurst(Level level, ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        Holder<Enchantment> windBurst = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST);
        return EnchantmentHelper.getItemEnchantmentLevel(windBurst, stack) > 0;
    }
}
