package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Set;

public final class ModEnchantments
{
    public static final ResourceKey<Enchantment> BEHEADING = of("beheading");
    public static final ResourceKey<Enchantment> TEMPERING = of("tempering");
    public static final ResourceKey<Enchantment> KEEPING = of("keeping");
    public static final ResourceKey<Enchantment> MAGMA_STRIDER = of("magma_strider");
    public static final ResourceKey<Enchantment> CRUST_WALKER = of("crust_walker");
    public static final ResourceKey<Enchantment> MELEE_PROTECTION = of("melee_protection");
    public static final ResourceKey<Enchantment> MOMENTUM = of("momentum");
    public static final ResourceKey<Enchantment> FLURRY = of("flurry");
    public static final ResourceKey<Enchantment> AIR_AFFINITY = of("air_affinity");
    public static final ResourceKey<Enchantment> SURGING = of("surging");

    // Held out of the current release: datagen skips these, so they never reach the registry. Remove from here and rerun datagen to re-enable
    public static final Set<ResourceKey<Enchantment>> DISABLED = Set.of(MOMENTUM, FLURRY, SURGING);

    public static boolean isEnabled(ResourceKey<Enchantment> key)
    {
        return !DISABLED.contains(key);
    }

    private static ResourceKey<Enchantment> of(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, NebajSEnchantmentRework.id(name));
    }

    public static void initialize() {}
}
