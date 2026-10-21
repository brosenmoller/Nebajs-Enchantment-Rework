package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nebaj.nebajsenchantmentrework.attribute.ModAttributes;
import com.nebaj.nebajsenchantmentrework.config.ModConfig;
import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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

    @ModifyReturnValue(method = "getXpNeededForNextLevel", at = @At("RETURN"))
    private int nebajsenchantmentrework$useFlatXpCurve(int original)
    {
        ModConfig.Experience experience = ModConfigManager.current().experience;
        return experience.flatXpCurve ? experience.xpPerLevel : original;
    }

    // Replaces vanilla's min(level * 7, 100) while keeping its keepInventory and spectator guards
    @WrapOperation(method = "getBaseExperienceReward", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int nebajsenchantmentrework$dropShareOfExperience(int levelReward, int cap, Operation<Integer> original)
    {
        ModConfig.Experience experience = ModConfigManager.current().experience;
        if (!experience.dropShareOnDeath) return original.call(levelReward, cap);

        Player self = (Player) (Object) this;
        return Mth.floor(nebajsenchantmentrework$totalPoints(self, experience) * experience.deathDropPercent / 100.0f);
    }

    @Unique
    private static float nebajsenchantmentrework$totalPoints(Player player, ModConfig.Experience experience)
    {
        if (experience.flatXpCurve) return (player.experienceLevel + player.experienceProgress) * experience.xpPerLevel;

        float points = player.experienceProgress * nebajsenchantmentrework$vanillaPointsForLevel(player.experienceLevel);
        for (int level = 0; level < player.experienceLevel; level++)
        {
            points += nebajsenchantmentrework$vanillaPointsForLevel(level);
        }
        return points;
    }

    // Mirrors vanilla's getXpNeededForNextLevel, which is replaced above while the flat curve is on
    @Unique
    private static int nebajsenchantmentrework$vanillaPointsForLevel(int level)
    {
        if (level >= 30) return 112 + (level - 30) * 9;
        if (level >= 15) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }

    // Vanilla divides by 5 when not on the ground (which includes riding); the divisor becomes 1 / airborne_mining_speed so the default 0.2 keeps vanilla behaviour
    @ModifyConstant(method = "getDestroySpeed", constant = @Constant(floatValue = 5.0F))
    private float nebajsenchantmentrework$applyAirborneMiningSpeed(float divisor)
    {
        Player self = (Player) (Object) this;
        float airborneMiningSpeed = (float) self.getAttributeValue(ModAttributes.AIRBORNE_MINING_SPEED);
        if (airborneMiningSpeed <= 0) return divisor;

        return 1.0F / airborneMiningSpeed;
    }
}
