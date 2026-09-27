package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public final class BookshelfScanner
{
    public static Set<Holder<Enchantment>> findEnchantedBooksInBookShelves(Level level, BlockPos tablePos)
    {
        Set<Holder<Enchantment>> foundSet = new HashSet<>();
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS)
        {
            BlockPos blockPos = tablePos.offset(offset);

            if (!(level.getBlockEntity(blockPos) instanceof ChiseledBookShelfBlockEntity shelf)) continue;

            for (int i = 0; i < shelf.getContainerSize(); i++)
            {
                ItemStack itemStack = shelf.getItem(i);
                if (!itemStack.is(Items.ENCHANTED_BOOK)) continue;

                ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                for (Holder<Enchantment> enchantmentHolder : itemEnchantments.keySet())
                {
                    if (enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK))
                    {
                        foundSet.add(enchantmentHolder);
                    }
                }
            }
        }

        return foundSet;
    }

    public static int countBookShelves(Level level, BlockPos tablePos)
    {
        int count = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS)
        {
            if (isValidBookShelf(level, tablePos, offset))
            {
                count++;
            }
        }
        return count;
    }

    private static boolean isValidBookShelf(Level level, BlockPos tablePos, BlockPos offset)
    {
        BlockPos shelfPos = tablePos.offset(offset);
        BlockState shelfState = level.getBlockState(shelfPos);

        boolean isProvider = shelfState.is(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                || isPoweredChiseledBookshelf(level, shelfPos, shelfState);

        if (!isProvider) return false;

        BlockPos transmitterPos = tablePos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
        boolean hasClearPath = level.getBlockState(transmitterPos).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
        return hasClearPath;
    }

    private static boolean isPoweredChiseledBookshelf(Level level, BlockPos pos, BlockState state)
    {
        if (!state.is(Blocks.CHISELED_BOOKSHELF)) return false;
        if (!(level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity chiseled)) return false;

        for (int i = 0; i < chiseled.getContainerSize(); i++)
        {
            if (chiseled.getItem(i).is(Items.ENCHANTED_BOOK))
            {
                return true;
            }
        }
        return false;
    }
}
