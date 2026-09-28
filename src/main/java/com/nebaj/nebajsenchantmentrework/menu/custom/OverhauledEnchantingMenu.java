package com.nebaj.nebajsenchantmentrework.menu.custom;

import com.nebaj.nebajsenchantmentrework.BookshelfScanner;
import com.nebaj.nebajsenchantmentrework.menu.ModMenuTypes;
import com.nebaj.nebajsenchantmentrework.network.SyncUnlockedEnchantmentsPayload;
import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
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
        boolean treasure,
        int registryId
    ) { }

    private static final int ENCHANTMENT_SLOT = 36;
    private static final int LAPIS_SLOT = 37;

    public ItemStack getEnchantItem() { return enchantSlots.getItem(0); }
    public int getLapisCount() { return enchantSlots.getItem(1).getCount(); }

    private static final Identifier EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    private List<EnchantmentEntry> enchantments = new ArrayList<>();
    private final ContainerLevelAccess access;
    private final Player player;
    private Set<Holder<Enchantment>> unlockedEnchantments = Set.of();
    private final Container enchantSlots = new SimpleContainer(2)
    {
        @Override
        public void setChanged()
        {
            super.setChanged();
            OverhauledEnchantingMenu.this.slotsChanged(this);
        }
    };

    public OverhauledEnchantingMenu(int containerId, Inventory inventory, BlockPos pos)
    {
        super(ModMenuTypes.OVERHAULED_ENCHANTING_TABLE_MENU, containerId);
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
        this.player = inventory.player;

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

    private Stream<Holder<Enchantment>> filterPool(Stream<Holder<Enchantment>> vanillaEnchantments)
    {
        return Stream.concat(
                vanillaEnchantments.filter(enchantmentHolder ->
                        !enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK)),
                unlockedEnchantments.stream()
        ).filter(enchantmentHolder -> !enchantmentHolder.is(EnchantmentTags.CURSE));
    }

    private void rescanUnlockedEnchantments()
    {
        access.execute((level, blockPos) -> unlockedEnchantments = BookshelfScanner.findEnchantedBooksInBookShelves(level, blockPos));
    }

    @Override
    public void slotsChanged(Container container)
    {
        if (container != enchantSlots) return;

        boolean isClientSide = access.evaluate((level, pos) -> level.isClientSide(), true);

        if (!isClientSide)
        {
            rescanUnlockedEnchantments();

            if (player instanceof ServerPlayer serverPlayer)
            {
                ServerPlayNetworking.send(serverPlayer, new SyncUnlockedEnchantmentsPayload(containerId, List.copyOf(unlockedEnchantments)));
            }
        }

        enchantments = buildEntries(enchantSlots.getItem(0));

        if (!isClientSide)
        {
            broadcastChanges();
        }
    }

    public void applySyncedUnlockedEnchantments(List<Holder<Enchantment>> unlocked)
    {
        this.unlockedEnchantments = Set.copyOf(unlocked);
        this.enchantments = buildEntries(enchantSlots.getItem(0));
    }

    public List<EnchantmentEntry> getEnchantments()
    {
        return enchantments;
    }

    private List<EnchantmentEntry> buildEntries(ItemStack itemStack)
    {
        if (itemStack.isEmpty() || !itemStack.has(DataComponents.ENCHANTABLE)) return List.of();

        return access.evaluate((level, pos) ->
        {
            RegistryAccess registryAccess = level.registryAccess();
            Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

            Stream<Holder<Enchantment>> applicable = getEnchantmentsForItem(registryAccess, itemStack);

            List<Holder<Enchantment>> pool = filterPool(applicable)
                    .sorted(Comparator.comparingInt(holder -> enchantmentRegistry.getId(holder.value())))
                    .toList();

            List<EnchantmentEntry> built = new ArrayList<>(pool.size());
            for (Holder<Enchantment> enchantmentHolder : pool)
            {
                boolean isTreasure = enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK);
                boolean unlocked = !isTreasure || unlockedEnchantments.contains(enchantmentHolder);
                int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantmentHolder, itemStack);
                int maxLevel = enchantmentHolder.value().getMaxLevel();
                int xpCost = 1; // TEMPORARY HARDCODED
                int materialCost = 1; // TEMPORARY HARDCODED
                int registryId = enchantmentRegistry.getId(enchantmentHolder.value());

                built.add(new EnchantmentEntry(
                    enchantmentHolder,
                    currentLevel,
                    maxLevel,
                    xpCost,
                    materialCost,
                    unlocked,
                    isTreasure,
                    registryId
                ));
            }
            return built;
        }, List.of());
    }

    private Stream<Holder<Enchantment>> getEnchantmentsForItem(RegistryAccess registryAccess, ItemStack itemStack)
    {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT)
            .listElements()
            .map(ref -> (Holder<Enchantment>) ref)
            .filter(holder -> holder.value().isPrimaryItem(itemStack));
    }

    public boolean canEnchant(EnchantmentEntry entry)
    {
        ItemStack itemStack = enchantSlots.getItem(0);
        ItemStack currency = enchantSlots.getItem(1);

        if (itemStack.isEmpty()) return false;
        if (!entry.unlocked()) return false;
        if (entry.currentLevel() >= entry.maxLevel()) return false;
        if (!isEnchantmentCompatibleWithExistingEnchantments(itemStack, entry.enchantment())) return false;

        if (player.hasInfiniteMaterials()) return true;

        if (player.experienceLevel < entry.xpCost()) return false;
        if (currency.getCount() < entry.materialCost()) return false;

        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId)
    {
        EnchantmentEntry enchantmentEntry = findEntryByRegistryId(buttonId).orElse(null);
        if (enchantmentEntry == null)
        {
            Util.logAndPauseIfInIde(player.getPlainTextName() + " pressed invalid or stale enchantment button id: " + buttonId);
            return false;
        }

        boolean isClientSide = access.evaluate((level, pos) -> level.isClientSide(), true);
        if (isClientSide) return false;

        if (!canEnchant(enchantmentEntry)) return false;

        ItemStack itemStack = this.enchantSlots.getItem(0);
        ItemStack currency = this.enchantSlots.getItem(1);

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

            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        });

        return true;
    }

    private Optional<EnchantmentEntry> findEntryByRegistryId(int registryId)
    {
        return enchantments.stream()
                .filter(entry -> entry.registryId() == registryId)
                .findFirst();
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
