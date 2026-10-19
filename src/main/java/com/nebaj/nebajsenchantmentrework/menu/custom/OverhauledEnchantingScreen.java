package com.nebaj.nebajsenchantmentrework.menu.custom;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryConfig;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu.EnchantmentEntry;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu.EntryCost;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

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
    private static final int MATERIAL_COST_DISABLED_OVERLAY_COLOR = 0x80555555;
    private static final int GHOST_ITEM_OVERLAY_COLOR = 0x30FFFFFF;
    private static final int SCROLLBAR_TRACK_COLOR = 0xFF555555;
    private static final int ITEM_ICON_SIZE = 16;
    private static final long MATERIAL_CYCLE_MILLIS = 1000L;
    private static final List<Holder<Item>> LAPIS_ONLY = List.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.LAPIS_LAZULI));

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
    private Item observedItem = Items.AIR;

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
        Item currentItem = this.menu.getEnchantItem().getItem();

        if (currentItem != this.observedItem)
        {
            this.observedItem = currentItem;
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
            boolean isLockedPlaceholder = entry.disabledReason() == OverhauledEnchantingMenu.DisabledReason.LOCKED;

            int displayedLevel = Math.max(1, Math.min(entry.currentLevel() + 1, entry.maxLevel()));
            Component name = isLockedPlaceholder
                    ? Component.translatable("gui.nebajs-enchantment-rework.unknown_enchantment")
                    : Enchantment.getFullname(entry.enchantment(), displayedLevel);
            String xpText = isLockedPlaceholder ? "" : String.valueOf(entry.cost().xp());

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

            if (!isLockedPlaceholder)
            {
                extractMaterialCost(graphics, entry.cost(), optionX + 1, optionY + 1, enabled);
            }

            graphics.textWithWordWrap(this.font, name.copy().withStyle(name.getStyle().withColor(textColor)), optionX + 20, optionY + 2, textWidth, textColor, false);

            if (!isLockedPlaceholder)
            {
                graphics.text(this.font, xpText, optionX + 20 + 86 - this.font.width(xpText), optionY + 9, enabled ? TEXT_COLOR_XP_COST_ENABLED : TEXT_COLOR_XP_COST_DISABLED);
            }
        }

        renderScrollBar(graphics, left, top, entries.size());
    }

    private void extractMaterialCost(GuiGraphicsExtractor graphics, EntryCost cost, int x, int y, boolean enabled)
    {
        ItemStack materialStack = cycledItem(cost.material().stream().toList());
        graphics.item(materialStack, x, y);
        graphics.itemDecorations(this.font, materialStack, x, y, String.valueOf(cost.materialCount()));

        if (!enabled)
        {
            graphics.fill(x, y, x + ITEM_ICON_SIZE, y + ITEM_ICON_SIZE, MATERIAL_COST_DISABLED_OVERLAY_COLOR);
        }
    }

    private static ItemStack cycledItem(List<Holder<Item>> items)
    {
        if (items.isEmpty()) return ItemStack.EMPTY;
        int index = (int) (Util.getMillis() / MATERIAL_CYCLE_MILLIS % items.size());
        return new ItemStack(items.get(index));
    }

    @Override
    protected void extractSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        List<Holder<Item>> expectedMaterials = expectedMaterials(mouseX, mouseY);
        boolean lapisOnly = expectedMaterials.equals(LAPIS_ONLY);
        this.menu.setShowLapisGhost(lapisOnly);

        super.extractSlots(graphics, mouseX, mouseY);

        Slot materialSlot = this.menu.getSlot(OverhauledEnchantingMenu.MATERIAL_SLOT);
        if (lapisOnly || materialSlot.hasItem()) return;

        graphics.fakeItem(cycledItem(expectedMaterials), materialSlot.x, materialSlot.y);
        graphics.fill(materialSlot.x, materialSlot.y, materialSlot.x + ITEM_ICON_SIZE, materialSlot.y + ITEM_ICON_SIZE, GHOST_ITEM_OVERLAY_COLOR);
    }

    private List<Holder<Item>> expectedMaterials(int mouseX, int mouseY)
    {
        Optional<EnchantmentEntry> hovered = hoveredEntry(mouseX, mouseY).filter(OverhauledEnchantingScreen::isPurchasable);
        if (hovered.isPresent()) return hovered.get().cost().material().stream().toList();

        List<Holder<Item>> materials = this.menu.getEnchantments().stream()
                .filter(OverhauledEnchantingScreen::isPurchasable)
                .flatMap(entry -> entry.cost().material().stream())
                .distinct()
                .toList();
        return materials.isEmpty() ? LAPIS_ONLY : materials;
    }

    private static boolean isPurchasable(EnchantmentEntry entry)
    {
        return entry.disabledReason() == OverhauledEnchantingMenu.DisabledReason.NONE;
    }

    private Optional<EnchantmentEntry> hoveredEntry(int mouseX, int mouseY)
    {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        List<EnchantmentEntry> entries = this.menu.getEnchantments();

        for (int row = 0; row < VISIBLE_OPTIONS; row++)
        {
            int index = this.scrollOffset + row;
            if (index >= entries.size()) break;
            if (isMouseOverOption(mouseX, mouseY, left, top, row)) return Optional.of(entries.get(index));
        }
        return Optional.empty();
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
        EnchantmentEntry entry = hoveredEntry(mouseX, mouseY).orElse(null);
        if (entry == null) return;

        if (entry.disabledReason() == OverhauledEnchantingMenu.DisabledReason.LOCKED)
        {
            List<Component> lockedTooltip = Lists.newArrayList();
            lockedTooltip.add(Component.translatable("gui.nebajs-enchantment-rework.unknown_enchantment").withStyle(ChatFormatting.WHITE));
            lockedTooltip.add(CommonComponents.EMPTY);
            lockedTooltip.add(Component.translatable("gui.nebajs-enchantment-rework.locked_treasure_explanation").withStyle(ChatFormatting.GRAY));
            entry.clueKey().ifPresent(key ->
                    lockedTooltip.add(Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
            graphics.setComponentTooltipForNextFrame(this.font, lockedTooltip, mouseX, mouseY);
            return;
        }

        List<Component> tooltip = Lists.newArrayList();

        int displayedLevel = Math.max(1, Math.min(entry.currentLevel() + 1, entry.maxLevel()));

        tooltip.add(Enchantment.getFullname(entry.enchantment(), displayedLevel).copy().withStyle(ChatFormatting.WHITE));
        if (entry.currentLevel() > 0)
        {
            tooltip.add(Component.translatable("gui.nebajs-enchantment-rework.current_level", entry.currentLevel()).withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(CommonComponents.EMPTY);

        addCostLines(tooltip, entry);

        switch (entry.disabledReason())
        {
            case MAX_LEVEL -> tooltip.add(Component.translatable("gui.nebajs-enchantment-rework.max_level").withStyle(ChatFormatting.RED));
            case INCOMPATIBLE -> entry.conflictingEnchantment().ifPresent(conflict ->
                    tooltip.add(Component.translatable("gui.nebajs-enchantment-rework.incompatible_with", conflict.value().description())
                            .withStyle(ChatFormatting.RED)));
            case NONE, LOCKED -> { }
        }

        graphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
    }

    private void addCostLines(List<Component> tooltip, EnchantmentEntry entry)
    {
        if (entry.disabledReason() == OverhauledEnchantingMenu.DisabledReason.MAX_LEVEL) return;

        EntryCost cost = entry.cost();
        ItemStack materialStack = this.menu.getMaterialStack();
        boolean infinite = this.minecraft.player.hasInfiniteMaterials();
        boolean hasXp = infinite || this.minecraft.player.experienceLevel >= cost.xp();
        boolean hasMaterial = infinite || (materialStack.is(cost.material()::contains) && materialStack.getCount() >= cost.materialCount());

        MutableComponent experienceComponent = Component.translatable(
                cost.xp() == 1 ? "container.enchant.level.one" : "container.enchant.level.many", cost.xp());
        tooltip.add(experienceComponent.withStyle(hasXp ? ChatFormatting.GRAY : ChatFormatting.RED));

        MutableComponent materialComponent = EnchantmentEntryConfig.isLapis(cost.material())
                ? Component.translatable(cost.materialCount() == 1 ? "container.enchant.lapis.one" : "container.enchant.lapis.many", cost.materialCount())
                : Component.translatable("gui.nebajs-enchantment-rework.material_cost",
                        cost.materialCount(), cycledItem(cost.material().stream().toList()).getHoverName());
        tooltip.add(materialComponent.withStyle(hasMaterial ? ChatFormatting.GRAY : ChatFormatting.RED));

        if (cost.requiredBookshelves() > 0)
        {
            boolean hasBookshelves = cost.currentBookshelves() >= cost.requiredBookshelves();
            tooltip.add(Component.translatable("gui.nebajs-enchantment-rework.bookshelves_cost",
                    cost.currentBookshelves(), cost.requiredBookshelves())
                .withStyle(hasBookshelves ? ChatFormatting.GRAY : ChatFormatting.RED));
        }
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