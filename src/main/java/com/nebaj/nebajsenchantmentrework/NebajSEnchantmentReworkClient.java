package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.menu.ModMenuTypes;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingScreen;
import com.nebaj.nebajsenchantmentrework.network.OverhauledEnchantingTableNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class NebajSEnchantmentReworkClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        MenuScreens.register(ModMenuTypes.OVERHAULED_ENCHANTING_TABLE_MENU, OverhauledEnchantingScreen::new);
        OverhauledEnchantingTableNetworking.registerClient();
    }
}
