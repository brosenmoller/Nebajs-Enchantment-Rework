package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.attachment.SurgingState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public final class MiningSpeedEnchantments
{
    public static final float FULL_CHARGE = 1.0F;

    // Exponential ramp towards these; level V's x190 lets a diamond pickaxe (speed 8) reach 1520, past obsidian's instamine threshold of 1500
    private static final double[] MOMENTUM_MAX_MULTIPLIERS = {3.0, 8.0, 25.0, 70.0, 190.0};
    private static final float MOMENTUM_BLOCKS_TO_MAX_AT_LEVEL_ONE = 40.0F;
    public static final long MOMENTUM_IDLE_TICKS = 40;

    public static final float SURGING_DRAIN_PER_TICK = 1.0F / 60;
    public static final float SURGING_DRAIN_PER_BLOCK = 1.0F / 15;
    // Bridges vanilla's 5 tick delay between consecutively mined blocks so a continuous surge isn't cut into separate ones
    public static final long SURGING_GRACE_TICKS = 10;
    private static final int SURGING_BASE_RECOVERY_TICKS = 240;
    private static final int SURGING_RECOVERY_TICKS_SAVED_PER_LEVEL = 40;
    private static final double SURGING_BASE_PEAK_BONUS = 20.0;
    private static final double SURGING_PEAK_BONUS_PER_LEVEL = 20.0;

    public record SurgingStatus(float charge, boolean coolingDown)
    {
        public static final SurgingStatus FULL = new SurgingStatus(FULL_CHARGE, false);
    }

    private MiningSpeedEnchantments() {}

    public static float momentumBuildUp(int count, int level)
    {
        if (level <= 0) return 0.0F;

        return Math.min(1.0F, count * level / MOMENTUM_BLOCKS_TO_MAX_AT_LEVEL_ONE);
    }

    public static double momentumMultiplier(int count, int level)
    {
        if (level <= 0) return 1.0;

        double maxMultiplier = MOMENTUM_MAX_MULTIPLIERS[Math.min(level, MOMENTUM_MAX_MULTIPLIERS.length) - 1];
        return Math.pow(maxMultiplier, momentumBuildUp(count, level));
    }

    public static int surgingRecoveryTicks(int level)
    {
        return SURGING_BASE_RECOVERY_TICKS - SURGING_RECOVERY_TICKS_SAVED_PER_LEVEL * Math.max(level, 1);
    }

    public static double surgingPeakBonus(int level)
    {
        return SURGING_BASE_PEAK_BONUS + SURGING_PEAK_BONUS_PER_LEVEL * level;
    }

    // The refill is derived from timestamps rather than ticked, so it keeps progressing for pickaxes that aren't held and the client can animate it without per-tick syncing
    public static SurgingStatus surgingStatus(SurgingState state, long now)
    {
        if (state == null) return SurgingStatus.FULL;
        if (!state.coolingDown() && now - state.stamp() <= SURGING_GRACE_TICKS) return new SurgingStatus(state.charge(), false);

        long cooldownStart = state.coolingDown() ? state.stamp() : state.stamp() + SURGING_GRACE_TICKS;
        float recovered = state.charge() + Math.max(0L, now - cooldownStart) / (float) state.recoveryTicks();
        if (recovered >= FULL_CHARGE) return SurgingStatus.FULL;

        return new SurgingStatus(recovered, true);
    }

    public static int mainHandLevel(Player player, ResourceKey<Enchantment> enchantment)
    {
        Optional<Holder.Reference<Enchantment>> holder = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment);
        if (holder.isEmpty()) return 0;

        return EnchantmentHelper.getItemEnchantmentLevel(holder.get(), player.getMainHandItem());
    }
}
