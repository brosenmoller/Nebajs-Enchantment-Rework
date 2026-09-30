package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryConfig;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryDataLoader;
import com.nebaj.nebajsenchantmentrework.data.XpSettings;
import com.nebaj.nebajsenchantmentrework.data.XpSettingsLoader;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Targets GrindstoneMenu's anonymous result slot, which is where vanilla computes the XP refund
@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneResultSlotMixin
{
    @Inject(method = "getExperienceFromItem", at = @At("HEAD"), cancellable = true)
    private void nebajsenchantmentrework$refundShareOfTableCost(ItemStack item, CallbackInfoReturnable<Integer> cir)
    {
        XpSettings settings = XpSettingsLoader.INSTANCE.current();
        int purchaseLevels = 0;

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(item).entrySet())
        {
            Holder<Enchantment> enchantment = entry.getKey();
            if (enchantment.is(EnchantmentTags.CURSE)) continue;

            EnchantmentEntryConfig config = EnchantmentEntryDataLoader.INSTANCE
                .getOrFallback(enchantment.unwrapKey().orElseThrow().identifier(), enchantment.value().getMaxLevel());

            for (int level = 1; level <= entry.getIntValue(); level++)
            {
                purchaseLevels += config.costForLevel(level).xpCost();
            }
        }

        cir.setReturnValue(Mth.floor(purchaseLevels * settings.xpPerLevel() * settings.grindstoneRefundFraction()));
    }

    // Vanilla returns a random 50-100% of the summed amount; return the full sum so the refund is exactly the configured share
    @ModifyReturnValue(method = "getExperienceAmount", at = @At("RETURN"))
    private int nebajsenchantmentrework$skipRandomHalving(int original, @Local(name = "amount") int amount)
    {
        return amount;
    }
}
