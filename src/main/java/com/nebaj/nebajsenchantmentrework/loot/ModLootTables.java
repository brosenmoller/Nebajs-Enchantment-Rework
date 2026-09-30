package com.nebaj.nebajsenchantmentrework.loot;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModLootTables
{
    public static void initialize()
    {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, NebajSEnchantmentRework.id("not_hardcore"), NotHardcoreCondition.MAP_CODEC);

        BeheadingLootModifier.initialize();
        TreasureLocationLootModifier.initialize();
    }
}
