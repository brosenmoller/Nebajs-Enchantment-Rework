package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;

public class ModEvents {
    public static void initialize() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos blockPos = hitResult.getBlockPos();

            if (player.isSpectator()) return InteractionResult.PASS;
            if (!level.getBlockState(blockPos).is(Blocks.ENCHANTING_TABLE)) return InteractionResult.PASS;
            if (player.isSecondaryUseActive()) return InteractionResult.PASS;

            if (level.isClientSide()) return InteractionResult.SUCCESS;

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new ExtendedMenuProvider<BlockPos>() {
                    @Override
                    public BlockPos getScreenOpeningData(ServerPlayer serverPlayer) {
                        return blockPos;
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.enchant");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p) {
                        return new OverhauledEnchantingMenu(id, inventory, blockPos);
                    }
                });
            }
            return InteractionResult.CONSUME;
        });
    }
}