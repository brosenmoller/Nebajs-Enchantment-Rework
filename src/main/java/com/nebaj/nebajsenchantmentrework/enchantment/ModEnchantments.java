package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments
{
    public static final ResourceKey<Enchantment> BEHEADING = of("beheading");
    public static final ResourceKey<Enchantment> TEMPERING = of("tempering");

    private static ResourceKey<Enchantment> of(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
    }

    public static void initialize() {}
}
