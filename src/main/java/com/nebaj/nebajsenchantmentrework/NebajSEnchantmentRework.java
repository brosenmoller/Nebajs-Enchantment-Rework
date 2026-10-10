package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.attachment.ModAttachments;
import com.nebaj.nebajsenchantmentrework.attribute.ModAttributes;
import com.nebaj.nebajsenchantmentrework.block.ModBlocks;
import com.nebaj.nebajsenchantmentrework.component.ModDataComponents;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryDataLoader;
import com.nebaj.nebajsenchantmentrework.data.XpSettingsLoader;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.event.ModEvents;
import com.nebaj.nebajsenchantmentrework.item.ModItemComponents;
import com.nebaj.nebajsenchantmentrework.loot.ModLootTables;
import com.nebaj.nebajsenchantmentrework.menu.ModMenuTypes;
import com.nebaj.nebajsenchantmentrework.network.OverhauledEnchantingTableNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebajSEnchantmentRework implements ModInitializer
{
    public static final String MOD_ID = "nebajs-enchantment-rework";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize()
    {
        ModAttributes.initialize();
        ModAttachments.initialize();
        ModDataComponents.initialize();
        ModBlocks.initialize();
        ModItemComponents.initialize();
        ModEnchantments.initialize();
        ModLootTables.initialize();
        ModEvents.initialize();
        ModMenuTypes.registerModMenuTypes();
        OverhauledEnchantingTableNetworking.registerCommon();

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id(EnchantmentEntryDataLoader.FOLDER), EnchantmentEntryDataLoader.INSTANCE);
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id(XpSettingsLoader.FOLDER), XpSettingsLoader.INSTANCE);
    }

    public static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
