package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    // Kept items stay in the dead player's inventory (so they survive a logout on the death screen) and are moved to the respawned player in ModEvents
    @WrapOperation(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    private void nebajsenchantmentrework$holdBackKeepingItems(Inventory inventory, Operation<Void> original)
    {
        Holder<Enchantment> keeping = ((Player) (Object) this).registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.KEEPING);
        Map<Integer, ItemStack> keptItems = new HashMap<>();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++)
        {
            ItemStack stack = inventory.getItem(slot);
            int levelOfKeeping = EnchantmentHelper.getItemEnchantmentLevel(keeping, stack);
            if (levelOfKeeping <= 0) continue;

            EnchantmentHelper.updateEnchantments(stack, enchantments -> enchantments.set(keeping, levelOfKeeping - 1));
            keptItems.put(slot, inventory.removeItemNoUpdate(slot));
        }

        original.call(inventory);
        keptItems.forEach(inventory::setItem);
    }
}
