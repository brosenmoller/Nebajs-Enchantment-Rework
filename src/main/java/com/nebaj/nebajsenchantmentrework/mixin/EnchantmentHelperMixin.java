package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.enchantment.LocationRestrictedEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.Stream;

// Level-based rolls (enchant_with_levels loot, traded equipment, mob spawn gear) never grant location-restricted enchantments.
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin
{
    @ModifyVariable(method = "selectEnchantment", at = @At("HEAD"), argsOnly = true)
    private static Stream<Holder<Enchantment>> nebajsenchantmentrework$excludeLocationRestricted(Stream<Holder<Enchantment>> candidates)
    {
        return LocationRestrictedEnchantments.withoutRestricted(candidates);
    }
}
