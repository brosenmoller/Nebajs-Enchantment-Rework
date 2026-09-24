package com.nebaj.nebajsenchantmentrework.menu.custom;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.TestBlockEditScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OverhauledEnchantingTableScreen extends AbstractContainerScreen<OverhauledEnchantingTableMenu>
{
    private static final Identifier TEXTURE =
            NebajSEnchantmentRework.id("textures/gui/overhauledenchantingtable/overhauledenchantingtable_gui.png");

    public OverhauledEnchantingTableScreen(OverhauledEnchantingTableMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a){
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
