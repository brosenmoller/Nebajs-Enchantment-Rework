package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu
{
    @Unique
    private static final float ANVIL_DAMAGE_CHANCE = 0.12F;

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private boolean onlyRenaming;

    @Shadow
    private int repairItemCountCost;

    @Shadow
    private String itemName;

    private AnvilMenuMixin(MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition)
    {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    /**
     * @author Nebaj
     * @reason Vanilla requires a cost above zero, which blocks free repairs and renames
     */
    @Overwrite
    protected boolean mayPickup(Player player, boolean hasItem)
    {
        return hasItem && (player.hasInfiniteMaterials() || player.experienceLevel >= cost.get());
    }

    /**
     * @author Nebaj
     * @reason Rewritten for the anvil XP rework
     */
    @Overwrite
    protected void onTake(Player player, ItemStack carried)
    {
        if (!player.hasInfiniteMaterials()) player.giveExperienceLevels(-cost.get());

        nebajsenchantmentrework$consumeAddition();
        cost.set(0);

        if (player instanceof ServerPlayer serverPlayer && nebajsenchantmentrework$isNameChanged(inputSlots.getItem(AnvilMenu.INPUT_SLOT)))
        {
            serverPlayer.getTextFilter().processStreamMessage(itemName);
        }

        inputSlots.setItem(AnvilMenu.INPUT_SLOT, ItemStack.EMPTY);
        access.execute((level, pos) -> nebajsenchantmentrework$damageAnvil(player, level, pos));
    }

    @Unique
    private void nebajsenchantmentrework$consumeAddition()
    {
        if (repairItemCountCost <= 0)
        {
            if (!onlyRenaming) inputSlots.setItem(AnvilMenu.ADDITIONAL_SLOT, ItemStack.EMPTY);
            return;
        }

        ItemStack addition = inputSlots.getItem(AnvilMenu.ADDITIONAL_SLOT);
        if (addition.isEmpty() || addition.getCount() <= repairItemCountCost)
        {
            inputSlots.setItem(AnvilMenu.ADDITIONAL_SLOT, ItemStack.EMPTY);
            return;
        }

        addition.shrink(repairItemCountCost);
        inputSlots.setItem(AnvilMenu.ADDITIONAL_SLOT, addition);
    }

    @Unique
    private void nebajsenchantmentrework$damageAnvil(Player player, Level level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        boolean shouldDamage = !player.hasInfiniteMaterials()
            && state.is(BlockTags.ANVIL)
            && player.getRandom().nextFloat() < ANVIL_DAMAGE_CHANCE;

        if (!shouldDamage)
        {
            level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
            return;
        }

        BlockState damagedState = AnvilBlock.damage(state);
        if (damagedState == null)
        {
            level.removeBlock(pos, false);
            level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
            return;
        }

        level.setBlock(pos, damagedState, Block.UPDATE_CLIENTS);
        level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
    }

    // Cancelled HEAD inject rather than @Overwrite: fabric-item-api-v1 redirects a call inside vanilla's createResult body and crashes if that body is replaced
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void nebajsenchantmentrework$createResult(CallbackInfo ci)
    {
        ci.cancel();
        onlyRenaming = false;

        ItemStack input = inputSlots.getItem(AnvilMenu.INPUT_SLOT);
        if (input.isEmpty() || !EnchantmentHelper.canStoreEnchantments(input))
        {
            nebajsenchantmentrework$clearResult();
            return;
        }

        repairItemCountCost = 0;
        ItemStack result = input.copy();
        ItemStack addition = inputSlots.getItem(AnvilMenu.ADDITIONAL_SLOT);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        long priorWorkTax = (long) input.getOrDefault(DataComponents.REPAIR_COST, 0) + addition.getOrDefault(DataComponents.REPAIR_COST, 0);

        int enchantPrice = 0;
        if (!addition.isEmpty())
        {
            OptionalInt combinePrice = nebajsenchantmentrework$isMaterialRepair(input, result, addition)
                ? nebajsenchantmentrework$repairWithMaterial(result, addition)
                : nebajsenchantmentrework$combineWithItemOrBook(input, result, addition, enchantments);

            if (combinePrice.isEmpty())
            {
                nebajsenchantmentrework$clearResult();
                return;
            }

            enchantPrice = combinePrice.getAsInt();
        }

        boolean renamed = nebajsenchantmentrework$applyRename(input, result);
        boolean repaired = result.getDamageValue() != input.getDamageValue();
        boolean enchanted = enchantPrice > 0;

        if (!enchanted && !repaired && !renamed)
        {
            nebajsenchantmentrework$clearResult();
            return;
        }

        onlyRenaming = !enchanted && !repaired;
        cost.set(enchanted ? (int) Mth.clamp(priorWorkTax + enchantPrice, 1L, Integer.MAX_VALUE) : 0);

        nebajsenchantmentrework$finalizeResult(result, addition, enchantments, enchanted);
        resultSlots.setItem(0, result);
        broadcastChanges();
    }

    @Unique
    private boolean nebajsenchantmentrework$isMaterialRepair(ItemStack input, ItemStack result, ItemStack addition)
    {
        return result.isDamageableItem() && input.isValidRepairItem(addition);
    }

    @Unique
    private OptionalInt nebajsenchantmentrework$repairWithMaterial(ItemStack result, ItemStack addition)
    {
        if (nebajsenchantmentrework$materialRepairAmount(result) <= 0) return OptionalInt.empty();

        int materialsUsed = 0;
        while (materialsUsed < addition.getCount())
        {
            int repairAmount = nebajsenchantmentrework$materialRepairAmount(result);
            if (repairAmount <= 0) break;

            result.setDamageValue(result.getDamageValue() - repairAmount);
            materialsUsed++;
        }

        repairItemCountCost = materialsUsed;
        return OptionalInt.of(0);
    }

    @Unique
    private int nebajsenchantmentrework$materialRepairAmount(ItemStack result)
    {
        Holder<Enchantment> tempering = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.TEMPERING);
        int levelOfTempering = result.getEnchantments().getLevel(tempering);
        return Math.min(result.getDamageValue(), result.getMaxDamage() / (4 - levelOfTempering));
    }

    @Unique
    private OptionalInt nebajsenchantmentrework$combineWithItemOrBook(ItemStack input, ItemStack result, ItemStack addition, ItemEnchantments.Mutable enchantments)
    {
        boolean usingBook = addition.has(DataComponents.STORED_ENCHANTMENTS);
        boolean isSameDamageableItem = result.is(addition.getItem()) && result.isDamageableItem();
        if (!usingBook && !isSameDamageableItem) return OptionalInt.empty();

        if (!usingBook) nebajsenchantmentrework$repairWithSacrifice(input, result, addition);

        int price = 0;
        boolean anyCompatible = false;
        boolean anyIncompatible = false;

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(addition).entrySet())
        {
            Holder<Enchantment> enchantmentHolder = entry.getKey();
            Enchantment enchantment = enchantmentHolder.value();

            int conflicts = nebajsenchantmentrework$countConflicts(enchantmentHolder, enchantments);
            price += conflicts;

            boolean canApply = player.hasInfiniteMaterials() || input.is(Items.ENCHANTED_BOOK) || input.canBeEnchantedWith(enchantmentHolder, EnchantingContext.ACCEPTABLE);
            if (!canApply || conflicts > 0)
            {
                anyIncompatible = true;
                continue;
            }

            if (input.getCount() > 1) return OptionalInt.empty();

            anyCompatible = true;
            int level = Math.min(nebajsenchantmentrework$mergedLevel(enchantments.getLevel(enchantmentHolder), entry.getIntValue()), enchantment.getMaxLevel());
            enchantments.set(enchantmentHolder, level);

            int fee = usingBook ? Math.max(1, enchantment.getAnvilCost() / 2) : enchantment.getAnvilCost();
            price += fee * level;
        }

        if (anyIncompatible && !anyCompatible) return OptionalInt.empty();
        return OptionalInt.of(price);
    }

    @Unique
    private static void nebajsenchantmentrework$repairWithSacrifice(ItemStack input, ItemStack result, ItemStack addition)
    {
        int inputDurability = input.getMaxDamage() - input.getDamageValue();
        int additionDurability = addition.getMaxDamage() - addition.getDamageValue();
        int bonusDurability = result.getMaxDamage() * 12 / 100;
        int resultDamage = Math.max(0, result.getMaxDamage() - (inputDurability + additionDurability + bonusDurability));

        if (resultDamage < result.getDamageValue()) result.setDamageValue(resultDamage);
    }

    @Unique
    private static int nebajsenchantmentrework$countConflicts(Holder<Enchantment> enchantment, ItemEnchantments.Mutable existing)
    {
        int conflicts = 0;
        for (Holder<Enchantment> other : existing.keySet())
        {
            if (other.equals(enchantment)) continue;
            if (!Enchantment.areCompatible(enchantment, other)) conflicts++;
        }
        return conflicts;
    }

    @Unique
    private static int nebajsenchantmentrework$mergedLevel(int currentLevel, int additionLevel)
    {
        if (currentLevel == additionLevel) return additionLevel + 1;
        return Math.max(currentLevel, additionLevel);
    }

    @Unique
    private boolean nebajsenchantmentrework$applyRename(ItemStack input, ItemStack result)
    {
        if (StringUtil.isBlank(itemName))
        {
            if (!input.has(DataComponents.CUSTOM_NAME)) return false;

            result.remove(DataComponents.CUSTOM_NAME);
            return true;
        }

        if (itemName.equals(input.getHoverName().getString())) return false;

        result.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
        return true;
    }

    @Unique
    private boolean nebajsenchantmentrework$isNameChanged(ItemStack input)
    {
        return !StringUtil.isBlank(itemName) && !input.getHoverName().getString().equals(itemName);
    }

    @Unique
    private void nebajsenchantmentrework$finalizeResult(ItemStack result, ItemStack addition, ItemEnchantments.Mutable enchantments, boolean enchanted)
    {
        int repairCost = Math.max(result.getOrDefault(DataComponents.REPAIR_COST, 0), addition.getOrDefault(DataComponents.REPAIR_COST, 0));
        if (enchanted) repairCost = AnvilMenu.calculateIncreasedRepairCost(repairCost);

        result.set(DataComponents.REPAIR_COST, repairCost);
        EnchantmentHelper.setEnchantments(result, enchantments.toImmutable());
    }

    @Unique
    private void nebajsenchantmentrework$clearResult()
    {
        resultSlots.setItem(0, ItemStack.EMPTY);
        cost.set(0);
    }
}
