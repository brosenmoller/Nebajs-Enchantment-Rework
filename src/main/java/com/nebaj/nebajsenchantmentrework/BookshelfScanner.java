package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

import java.util.HashSet;
import java.util.Set;

public class BookshelfScanner
{
    public static Set<Holder<Enchantment>> scan(Level level, BlockPos tablePos)
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
}
