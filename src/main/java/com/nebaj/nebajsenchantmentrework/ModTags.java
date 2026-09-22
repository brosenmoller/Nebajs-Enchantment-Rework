package com.nebaj.nebajsenchantmentrework;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModTags {
    public static final TagKey<Enchantment> ENCHANTMENTTABLE_ENCHANTMENTS_REQUIRING_BOOK =
            TagKey.create(Registries.ENCHANTMENT,
                    NebajSEnchantmentRework.id("enchantmenttable_enchantments_requiring_book"));
}