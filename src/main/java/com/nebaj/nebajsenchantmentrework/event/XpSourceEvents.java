package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.config.ModConfig;
import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import com.nebaj.nebajsenchantmentrework.data.XpSettingsLoader;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

public final class XpSourceEvents
{
    private XpSourceEvents() { }

    public static void initialize()
    {
        PlayerBlockBreakEvents.AFTER.register(XpSourceEvents::awardBlockBreakXp);
    }

    public static void awardBerryHarvestXp(Level level, BlockPos pos)
    {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!ModConfigManager.current().experience.berryHarvestXp) return;

        int amount = XpSettingsLoader.INSTANCE.current().berryHarvestXp().sample(serverLevel.getRandom());
        if (amount > 0) ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), amount);
    }

    private static void awardBlockBreakXp(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity)
    {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (player.isCreative()) return;
        if (!isBlockBreakXpEnabled(state)) return;
        if (!isFullyGrown(state)) return;

        IntProvider xp = XpSettingsLoader.INSTANCE.current().blockBreakXp().get(state.getBlock());
        if (xp == null) return;

        int amount = EnchantmentHelper.processBlockExperience(serverLevel, player.getMainHandItem(), xp.sample(serverLevel.getRandom()));
        if (amount > 0) ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), amount);
    }

    private static boolean isBlockBreakXpEnabled(BlockState state)
    {
        ModConfig.Experience experience = ModConfigManager.current().experience;
        return state.is(Blocks.AMETHYST_CLUSTER) ? experience.amethystXp : experience.cropHarvestXp;
    }

    private static boolean isFullyGrown(BlockState state)
    {
        for (Property<?> property : state.getProperties())
        {
            if (!(property instanceof IntegerProperty age) || !age.getName().equals("age")) continue;

            return state.getValue(age) == Collections.max(age.getPossibleValues()).intValue();
        }
        return true;
    }
}
