package com.nebaj.nebajsenchantmentrework.menu.custom;

import com.nebaj.nebajsenchantmentrework.BookshelfScanner;
import com.nebaj.nebajsenchantmentrework.menu.ModMenuTypes;
import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.stream.Stream;

public class OverhauledEnchantingMenu extends AbstractContainerMenu
{
    public record EnchantmentEntry(
        Holder<Enchantment> enchantment,
        int currentLevel,
        int maxLevel,
        int xpCost,
        int materialCost,
        boolean unlocked,
        boolean treasure
    )
    {
    }

    private static final Identifier EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    private List<EnchantmentEntry> enchantments = new ArrayList<>();
    private final ContainerLevelAccess access;
    private final Container enchantSlots = new SimpleContainer(2);

    private Set<Holder<Enchantment>> unlockedEnchantments = Set.of();

    public OverhauledEnchantingMenu(int containerId, Inventory inventory, BlockPos pos)
    {
        super(ModMenuTypes.OVERHAULED_ENCHANTING_TABLE_MENU, containerId);
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47)
        {
            {
                Objects.requireNonNull(OverhauledEnchantingMenu.this);
            }

            public int getMaxStackSize()
            {
                return 1;
            }
        });
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47)
        {
            {
                Objects.requireNonNull(OverhauledEnchantingMenu.this);
            }

            public boolean mayPlace(final ItemStack itemStack)
            {
                return itemStack.is(Items.LAPIS_LAZULI);
            }
            public Identifier getNoItemIcon() { return EMPTY_SLOT_LAPIS_LAZULI; }
        });
    }

    private Stream<Holder<Enchantment>> filterPool(Stream<Holder<Enchantment>> vanilla)
    {
        return Stream.concat(
                vanilla.filter(enchantmentHolder ->
                        !enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK)),
                unlockedEnchantments.stream()
        );
    }

    private void rescanUnlockedEnchantments()
    {
        access.execute((level, blockPos) -> unlockedEnchantments = BookshelfScanner.findEnchantedBooksInBookShelves(level, blockPos));
    }

    @Override
    public void slotsChanged(Container container)
    {
        if (container != enchantSlots) return;

        ItemStack itemStack = enchantSlots.getItem(0);
        enchantments.clear();
        rebuildEnchantments(itemStack);
        broadcastChanges();
    }

    private void rebuildEnchantments(ItemStack itemStack)
    {
        rescanUnlockedEnchantments();

        if (itemStack.isEmpty() || !itemStack.isEnchantable()) return;

        this.access.execute((level, pos) ->
        {
            int bookcases = BookshelfScanner.countBookShelves(level, pos);

            Stream<Holder<Enchantment>> applicable = getEnchantmentsForItem(level.registryAccess(), itemStack);
            List<Holder<Enchantment>> pool = filterPool(applicable).toList();

            for (Holder<Enchantment> enchantmentHolder : pool)
            {
                boolean isTreasure = enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK);
                boolean unlocked = !isTreasure || unlockedEnchantments.contains(enchantmentHolder);
                int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantmentHolder, itemStack);
                int maxLevel = enchantmentHolder.value().getMaxLevel();
                int xpCost = 1; // TEMPORARY HARDCODED
                int materialCost = 1; // TEMPORARY HARDCODED

                enchantments.add(new EnchantmentEntry(
                    enchantmentHolder,
                    currentLevel,
                    maxLevel,
                    xpCost,
                    materialCost,
                    unlocked,
                    isTreasure
                ));
            }
        });
    }

    private Stream<Holder<Enchantment>> getEnchantmentsForItem(RegistryAccess registryAccess, ItemStack itemStack)
    {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT)
            .listElements()
            .map(ref -> (Holder<Enchantment>) ref)
            .filter(holder -> holder.value().isPrimaryItem(itemStack));
    }


    @Override
    public boolean clickMenuButton(Player player, int buttonId)
    {
        if (buttonId < 0 || buttonId >= enchantments.size())
        {
            Util.logAndPauseIfInIde(player.getPlainTextName() + " pressed invalid enchantment button id: " + buttonId);
            return false;
        }

        EnchantmentEntry enchantmentEntry = enchantments.get(buttonId);
        ItemStack itemStack = this.enchantSlots.getItem(0);
        ItemStack currency = this.enchantSlots.getItem(1);

        if (itemStack.isEmpty() || !enchantmentEntry.unlocked() || enchantmentEntry.currentLevel() >= enchantmentEntry.maxLevel()) return false;
        if (!isEnchantmentCompatibleWithExistingEnchantments(itemStack, enchantmentEntry.enchantment())) return false;

        boolean hasInfiniteMaterials = player.hasInfiniteMaterials();

        if (!hasInfiniteMaterials)
        {
            if (player.experienceLevel < enchantmentEntry.xpCost()) return false;
            if (currency.isEmpty() || currency.getCount() < enchantmentEntry.materialCost()) return false;
        }

        access.execute((level, pos) ->
        {
            ItemStack item = itemStack;

            player.onEnchantmentPerformed(item, enchantmentEntry.xpCost());
            item.enchant(enchantmentEntry.enchantment(), enchantmentEntry.currentLevel() + 1);

            currency.consume(enchantmentEntry.materialCost(), player);
            if (currency.isEmpty())
            {
                enchantSlots.setItem(1, ItemStack.EMPTY);
            }

            player.awardStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayer serverPlayer)
            {
                CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, item, enchantmentEntry.xpCost());
            }

            enchantSlots.setChanged();
            slotsChanged(enchantSlots);

            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        });

        return true;
    }

    private boolean isEnchantmentCompatibleWithExistingEnchantments(ItemStack itemStack, Holder<Enchantment> candidate)
    {
        ItemEnchantments existing = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        Set<Holder<Enchantment>> others = new HashSet<>(existing.keySet());
        others.remove(candidate);
        return EnchantmentHelper.isEnchantmentCompatible(others, candidate);
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        access.execute((level, pos) -> this.clearContainer(player, this.enchantSlots));
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(access, player, Blocks.ENCHANTING_TABLE);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex)
    {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
        {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false))
            {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT)
        {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
            {
                return ItemStack.EMPTY;
            }
        } else
        {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0)
        {
            sourceSlot.set(ItemStack.EMPTY);
        } else
        {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private void addPlayerInventory(Inventory playerInventory)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int l = 0; l < 9; ++l)
            {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory)
    {
        for (int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
