package com.nebaj.nebajsenchantmentrework.tags;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
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
        public static final TagKey<Enchantment> MINING_SPEED_EXCLUSIVE = createTag("exclusive_set/mining_speed");
        public static final TagKey<Enchantment> AIR_AFFINITY_EXCLUSIVE = createTag("exclusive_set/air_affinity");

        private static TagKey<Enchantment> createTag(String name)
        {
            return TagKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> BEHEADING_ENCHANTABLE = createTag("enchantable/beheading");
        public static final TagKey<Item> LOOTING_ENCHANTABLE = createTag("enchantable/looting");
        public static final TagKey<Item> FIRE_ASPECT_ENCHANTABLE = createTag("enchantable/fire_aspect");
        public static final TagKey<Item> SHARPNESS_ENCHANTABLE = createTag("enchantable/sharpness");
        public static final TagKey<Item> BOW_OR_CROSSBOW_ENCHANTABLE = createTag("enchantable/bow_or_crossbow");

        private static TagKey<Item> createTag(String name)
        {
            return TagKey.create(Registries.ITEM, NebajSEnchantmentRework.id(name));
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
