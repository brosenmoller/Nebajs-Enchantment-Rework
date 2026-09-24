package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.event.ModEvents;
import com.nebaj.nebajsenchantmentrework.loot.ModLootTables;
import com.nebaj.nebajsenchantmentrework.menu.ModMenuTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebajSEnchantmentRework implements ModInitializer
{
    public static final String MOD_ID = "nebajs-enchantment-rework";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize()
    {
        ModEnchantments.initialize();
        ModLootTables.initialize();
        ModEvents.initialize();
        ModMenuTypes.registerModMenuTypes();
    }

    public static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
