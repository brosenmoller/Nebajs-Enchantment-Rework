package com.nebaj.nebajsenchantmentrework.attribute;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class ModAttributes
{
    public static final Holder<Attribute> LAVA_MOVEMENT_EFFICIENCY = Registry.registerForHolder(
        BuiltInRegistries.ATTRIBUTE,
        NebajSEnchantmentRework.id("lava_movement_efficiency"),
        new RangedAttribute("attribute.name.nebajs-enchantment-rework.lava_movement_efficiency", 0.0, 0.0, 1.0).setSyncable(true)
    );

    public static final Holder<Attribute> AIRBORNE_MINING_SPEED = Registry.registerForHolder(
        BuiltInRegistries.ATTRIBUTE,
        NebajSEnchantmentRework.id("airborne_mining_speed"),
        new RangedAttribute("attribute.name.nebajs-enchantment-rework.airborne_mining_speed", 0.2, 0.0, 1.0).setSyncable(true)
    );

    private ModAttributes() {}

    public static void initialize() {}
}
