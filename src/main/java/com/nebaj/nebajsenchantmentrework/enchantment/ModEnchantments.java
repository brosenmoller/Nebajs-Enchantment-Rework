package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments
{
    public static final ResourceKey<Enchantment> BEHEADING = of("beheading");
    public static final ResourceKey<Enchantment> TEMPERING = of("tempering");
    public static final ResourceKey<Enchantment> KEEPING = of("keeping");
    public static final ResourceKey<Enchantment> MAGMA_STRIDER = of("magma_strider");
    public static final ResourceKey<Enchantment> CRUST_WALKER = of("crust_walker");
    public static final ResourceKey<Enchantment> MELEE_PROTECTION = of("melee_protection");

    private static ResourceKey<Enchantment> of(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
    }

    public static void initialize() {}
}
