package com.nebaj.nebajsenchantmentrework.tags;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModTags
{
    public static class Enchantments
    {
        public static final TagKey<Enchantment> ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK = createTag("enchantingtable_enchantments_requiring_book");
        public static final TagKey<Enchantment> LOCATION_RESTRICTED = createTag("location_restricted");
        public static final TagKey<Enchantment> TEMPERING_EXCLUSIVE = createTag("exclusive_set/tempering");
        public static final TagKey<Enchantment> KEEPING_EXCLUSIVE = createTag("exclusive_set/keeping");
        public static final TagKey<Enchantment> MAGMA_STRIDER_EXCLUSIVE = createTag("exclusive_set/magma_strider");
        public static final TagKey<Enchantment> CRUST_WALKER_EXCLUSIVE = createTag("exclusive_set/crust_walker");

        private static TagKey<Enchantment> createTag(String name)
        {
            return TagKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
        }
    }

    public static class DamageTypes
    {
        public static final TagKey<DamageType> IS_MELEE = createTag("is_melee");

        private static TagKey<DamageType> createTag(String name)
        {
            return TagKey.create(Registries.DAMAGE_TYPE, NebajSEnchantmentRework.id(name));
        }
    }
}
