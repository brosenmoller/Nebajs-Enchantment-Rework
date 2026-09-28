package com.nebaj.nebajsenchantmentrework.menu.custom;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu.EnchantmentEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OverhauledEnchantingScreen extends AbstractContainerScreen<OverhauledEnchantingMenu>
{
    private static final Identifier ENCHANTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final Identifier ENCHANTING_BOOK_LOCATION = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");

    private static final Identifier ENCHANTMENT_SLOT_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");

    private static final int OPTION_X = 60;
    private static final int OPTION_Y = 14;
    private static final int OPTION_WIDTH = 108;
    private static final int OPTION_HEIGHT = 19;
    private static final int VISIBLE_OPTIONS = 3;

    private static final int SCROLLER_X = 168;
    private static final int SCROLLER_Y = 14;
    private static final int SCROLLER_WIDTH = 5;
    private static final int SCROLLER_TRACK_HEIGHT = 56;
    private static final int SCROLLER_HEIGHT = 27;

    private static final int TEXT_COLOR_ENCHANTMENT_NAME_ENABLED = 0xFFFFFFFF;
    private static final int TEXT_COLOR_ENCHANTMENT_NAME_HOVERED = 0xFFFFFFFF;
    private static final int TEXT_COLOR_HALF_BRIGHTNESS_MASK = 0xFEFEFE;
    private static final int TEXT_COLOR_XP_COST_ENABLED = 0xFF80FF20;
    private static final int TEXT_COLOR_XP_COST_DISABLED = 0xFF407F10;
    private static final int TEXT_COLOR_MATERIAL_COST_ENABLED = 0xFF40C0FF;
    private static final int TEXT_COLOR_MATERIAL_COST_DISABLED = 0xFF20607F;
    private static final int SCROLLBAR_TRACK_COLOR = 0xFF555555;
    private static final int MATERIAL_COST_ICON_BOX_SIZE = 16;

    private final RandomSource random = RandomSource.create();

    private BookModel bookModel;

    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;

    public float open;
    public float oOpen;

    private ItemStack last = ItemStack.EMPTY;
    private int scrollOffset = 0;
    private List<EnchantmentEntry> observedEntries = List.of();

    public OverhauledEnchantingScreen(OverhauledEnchantingMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    protected void init()
    {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick()
    {
        super.containerTick();

        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
        List<EnchantmentEntry> current = this.menu.getEnchantments();

        if (current != this.observedEntries)
        {
            this.observedEntries = current;
            this.scrollOffset = 0;
        }

        clampScroll();
        tickBook();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ENCHANTING_TABLE_LOCATION,
                left,
                top,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256
        );

        extractBook(graphics, left, top);
        extractEnchantments(graphics, left, top, mouseX, mouseY);
    }

    private void extractEnchantments(GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY)
    {
        List<EnchantmentEntry> entries = this.menu.getEnchantments();

        for (int row = 0; row < VISIBLE_OPTIONS; row++)
        {
            int index = row + this.scrollOffset;
            int optionX = left + OPTION_X;
            int optionY = top + OPTION_Y + OPTION_HEIGHT * row;

            if (index >= entries.size())
            {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
                continue;
            }

            EnchantmentEntry entry = entries.get(index);
            boolean hovered = isMouseOverOption(mouseX, mouseY, left, top, row);
            boolean enabled = this.menu.canEnchant(entry);

            int displayedLevel = Math.max(1, Math.min(entry.currentLevel() + 1, entry.maxLevel()));
            Component name = Enchantment.getFullname(entry.enchantment(), displayedLevel);
            String xpText = String.valueOf(entry.xpCost());

            int textWidth = 86 - this.font.width(xpText);

            int textColor = TEXT_COLOR_ENCHANTMENT_NAME_ENABLED;

            if (!enabled)
            {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
                textColor = ARGB.opaque((textColor & TEXT_COLOR_HALF_BRIGHTNESS_MASK) >> 1);
            }
            else
            {
                if (hovered)
                {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
                    graphics.requestCursor(CursorTypes.POINTING_HAND);
                    textColor = TEXT_COLOR_ENCHANTMENT_NAME_HOVERED;
                }
                else
                {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_SPRITE, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
                }
            }

            String materialCostText = String.valueOf(entry.materialCost());
            int materialCostColor = enabled ? TEXT_COLOR_MATERIAL_COST_ENABLED : TEXT_COLOR_MATERIAL_COST_DISABLED;
            int materialCostX = optionX + 1 + (MATERIAL_COST_ICON_BOX_SIZE - this.font.width(materialCostText)) / 2;
            int materialCostY = optionY + 1 + (MATERIAL_COST_ICON_BOX_SIZE - this.font.lineHeight) / 2;
            graphics.text(this.font, materialCostText, materialCostX, materialCostY, materialCostColor);

            graphics.textWithWordWrap(this.font, name.copy().withStyle(name.getStyle().withColor(textColor)), optionX + 20, optionY + 2, textWidth, textColor, false);
            graphics.text(this.font, xpText, optionX + 20 + 86 - this.font.width(xpText), optionY + 9, enabled ? TEXT_COLOR_XP_COST_ENABLED : TEXT_COLOR_XP_COST_DISABLED);
        }

        renderScrollBar(graphics, left, top, entries.size());
    }

    private boolean isMouseOverOption(int mouseX, int mouseY, int left, int top, int row)
    {
        int x = left + OPTION_X;
        int y = top + OPTION_Y + OPTION_HEIGHT * row;
        return mouseX >= x && mouseY >= y && mouseX < x + OPTION_WIDTH && mouseY < y + OPTION_HEIGHT;
    }

    private void renderScrollBar(GuiGraphicsExtractor graphics, int left, int top, int entryCount)
    {
        int maxOffset = getMaxScroll();
        if (maxOffset <= 0) return;

        int x = left + SCROLLER_X;
        int y = top + SCROLLER_Y;
        float progress = maxOffset == 0 ? 0.0F : (float) this.scrollOffset / (float) maxOffset;

        int thumbY = y + (int) ((SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT) * progress);

        graphics.fill(x, y, x + SCROLLER_WIDTH, y + SCROLLER_TRACK_HEIGHT, SCROLLBAR_TRACK_COLOR);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, x, thumbY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    private void extractBook(GuiGraphicsExtractor graphics, int left, int top)
    {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float open = Mth.lerp(a, this.oOpen, this.open);
        float flip = Mth.lerp(a, this.oFlip, this.flip);
        int x0 = left + 14;
        int y0 = top + 14;
        int x1 = x0 + 38;
        int y1 = y0 + 31;

        graphics.book(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0F, open, flip, x0, y0, x1, y1);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignored)
    {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);

        super.extractRenderState(graphics, mouseX, mouseY, a);
        extractEnchantmentTooltip(graphics, mouseX, mouseY);
    }

    private void extractEnchantmentTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        int relativeX = mouseX - (left + OPTION_X);
        int relativeY = mouseY - (top + OPTION_Y);

        if (relativeX < 0 || relativeY < 0 || relativeX >= OPTION_WIDTH || relativeY >= OPTION_HEIGHT * VISIBLE_OPTIONS) return;

        int row = relativeY / OPTION_HEIGHT;
        int rowY = relativeY - row * OPTION_HEIGHT;

        if (rowY >= OPTION_HEIGHT) return;

        int index = this.scrollOffset + row;

        List<EnchantmentEntry> entries = this.menu.getEnchantments();

        if (index < 0 || index >= entries.size()) return;

        EnchantmentEntry entry = entries.get(index);
        if (!isMouseOverOption(mouseX, mouseY, left, top, row)) return;

        boolean infinite = this.minecraft.player.hasInfiniteMaterials();
        boolean hasXp = infinite || this.minecraft.player.experienceLevel >= entry.xpCost();
        boolean hasLapis = infinite || this.menu.getLapisCount() >= entry.materialCost();

        List<Component> tooltip = Lists.newArrayList();

        int displayedLevel = Math.max(1, Math.min(entry.currentLevel() + 1, entry.maxLevel()));

        tooltip.add(Enchantment.getFullname(entry.enchantment(), displayedLevel).copy().withStyle(ChatFormatting.WHITE));
        if (entry.currentLevel() > 0)
        {
            tooltip.add(Component.literal("Current level: " + entry.currentLevel()).withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(CommonComponents.EMPTY);

        MutableComponent experienceComponent = Component.literal(entry.xpCost() + " Experience Level" + (entry.xpCost() == 1 ? "" : "s"));
        tooltip.add(experienceComponent.withStyle(hasXp ? ChatFormatting.GRAY : ChatFormatting.RED));

        MutableComponent lapisComponent = Component.literal(entry.materialCost() + " Lapis Lazuli");
        tooltip.add(lapisComponent.withStyle(hasLapis ? ChatFormatting.GRAY : ChatFormatting.RED));

        if (!entry.unlocked())
        {
            tooltip.add(Component.literal("Locked").withStyle(ChatFormatting.RED));
        }

        if (entry.currentLevel() >= entry.maxLevel())
        {
            tooltip.add(Component.literal("Maximum level").withStyle(ChatFormatting.GRAY));
        }

        graphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        for (int row = 0; row < VISIBLE_OPTIONS; row++)
        {
            int x = left + OPTION_X;
            int y = top + OPTION_Y + OPTION_HEIGHT * row;

            double mouseX = event.x();
            double mouseY = event.y();

            if (mouseX >= x && mouseY >= y && mouseX < x + OPTION_WIDTH && mouseY < y + OPTION_HEIGHT)
            {
                int index = this.scrollOffset + row;

                List<EnchantmentEntry> entries = this.menu.getEnchantments();
                if (index < 0 || index >= entries.size()) return true;

                EnchantmentEntry entry = entries.get(index);
                if (!this.menu.canEnchant(entry)) return true;

                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, entry.registryId());

                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        int x = left + OPTION_X;
        int y = top + OPTION_Y;

        if (mouseX >= x && mouseX < x + OPTION_WIDTH && mouseY >= y && mouseY < y + OPTION_HEIGHT * VISIBLE_OPTIONS)
        {
            this.scrollOffset -= (int) verticalAmount;
            clampScroll();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private int getMaxScroll()
    {
        return Math.max(0, this.menu.getEnchantments().size() - VISIBLE_OPTIONS);
    }

    private void clampScroll()
    {
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, getMaxScroll());
    }

    private void tickBook()
    {
        ItemStack current = this.menu.getEnchantItem();

        if (!ItemStack.matches(current, this.last))
        {
            this.last = current;

            do
            {
                this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));
            }
            while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }

        this.oFlip = this.flip;
        this.oOpen = this.open;

        boolean shouldBeOpen = !this.menu.getEnchantments().isEmpty();

        if (shouldBeOpen)
        {
            this.open += 0.2F;
        }
        else
        {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float diff = (this.flipT - this.flip) * 0.4F;

        diff = Mth.clamp(diff, -0.2F, 0.2F);
        this.flipA += (diff - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }
}