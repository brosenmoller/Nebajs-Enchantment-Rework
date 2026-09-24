package com.nebaj.nebajsenchantmentrework.tags;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModTags
{
    public static class Enchantments
    {
        public static final TagKey<Enchantment> ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK =
                createTag("enchantingtable_enchantments_requiring_book");

        private static TagKey<Enchantment> createTag(String name)
        {
            return TagKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
        }
    }
}