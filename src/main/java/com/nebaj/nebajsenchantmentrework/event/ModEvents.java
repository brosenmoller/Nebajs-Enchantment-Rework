package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;

public class ModEvents
{
    public static void initialize()
    {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
        {
            BlockPos blockPos = hitResult.getBlockPos();

            if (player.isSpectator()) return InteractionResult.PASS;
            if (!level.getBlockState(blockPos).is(Blocks.ENCHANTING_TABLE)) return InteractionResult.PASS;
            if (player.isSecondaryUseActive()) return InteractionResult.PASS;

            if (level.isClientSide()) return InteractionResult.SUCCESS;

            if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(new ExtendedMenuProvider<BlockPos>()
                {
                    @Override
                    public BlockPos getScreenOpeningData(ServerPlayer serverPlayer)
                    {
                        return blockPos;
                    }

                    @Override
                    public Component getDisplayName()
                    {
                        return Component.translatable("container.enchant");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p)
                    {
                        return new OverhauledEnchantingMenu(id, inventory, blockPos);
                    }
                });
            }
            return InteractionResult.CONSUME;
        });

        ServerPlayerEvents.COPY_FROM.register(ModEvents::restoreKeepingItems);
    }

    private static void restoreKeepingItems(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive)
    {
        if (alive || oldPlayer.isSpectator()) return;
        if (newPlayer.level().getGameRules().get(GameRules.KEEP_INVENTORY)) return;

        Inventory oldInventory = oldPlayer.getInventory();
        Inventory newInventory = newPlayer.getInventory();
        for (int slot = 0; slot < oldInventory.getContainerSize(); slot++)
        {
            ItemStack stack = oldInventory.getItem(slot);
            if (stack.isEmpty()) continue;

            newInventory.setItem(slot, stack.copy());
        }
    }
}