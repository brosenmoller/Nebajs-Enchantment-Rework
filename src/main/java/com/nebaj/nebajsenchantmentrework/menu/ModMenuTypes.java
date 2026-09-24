package com.nebaj.nebajsenchantmentrework.menu;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingTableMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes
{
    public static final MenuType<OverhauledEnchantingTableMenu> OVERHAULED_ENCHANTING_TABLE_MENU =
            Registry.register(BuiltInRegistries.MENU,
                    NebajSEnchantmentRework.id("overhauled_enchanting_menu"),
                    new ExtendedMenuType<>(OverhauledEnchantingTableMenu::new, BlockPos.STREAM_CODEC)
            );

    public static void registerModMenuTypes(){

    }
}
