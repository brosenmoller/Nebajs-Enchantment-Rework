package com.nebaj.nebajsenchantmentrework;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class BookshelfScanner
{
    public static Set<Holder<Enchantment>> scan(Level level, BlockPos tablePos)
    {
        Logger log = LoggerFactory.getLogger("nebajsenchantmentrework");

        var tagContents = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModTags.ENCHANTMENTTABLE_ENCHANTMENTS_REQUIRING_BOOK);
        log.info("[scan] tag has {} entries", tagContents.map(t -> t.size()).orElse(-1));


        Set<Holder<Enchantment>> foundSet = new HashSet<>();
        int shelves = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS)
        {
            BlockPos blockPos = tablePos.offset(offset);

            if (!(level.getBlockEntity(blockPos) instanceof ChiseledBookShelfBlockEntity shelf)) { continue; }

            shelves++;

            for (int i = 0; i < shelf.getContainerSize(); i++)
            {
                ItemStack itemStack = shelf.getItem(i);
                if (!itemStack.is(Items.ENCHANTED_BOOK)) { continue; }

                ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                log.info("[scan] shelf {} slot {}: {} stored={}", blockPos, i, itemStack.getItem(), itemEnchantments.keySet().size());
                for (Holder<Enchantment> enchantmentHolder : itemEnchantments.keySet()) {
                    if (enchantmentHolder.is(ModTags.ENCHANTMENTTABLE_ENCHANTMENTS_REQUIRING_BOOK)) foundSet.add(enchantmentHolder);
                }
            }
        }
        log.info("[scan] chiseled shelves found={}, unlocked={}", shelves, foundSet.size());
        return foundSet;
    }
}
