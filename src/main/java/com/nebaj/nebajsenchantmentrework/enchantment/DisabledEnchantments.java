package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.stream.Stream;

public final class DisabledEnchantments
{
    private DisabledEnchantments() {}

    public static boolean isDisabled(ResourceKey<Enchantment> key)
    {
        return ModConfigManager.current().isEnchantmentDisabled(key.identifier());
    }

    public static boolean isDisabled(Holder<Enchantment> holder)
    {
        return holder.unwrapKey().map(DisabledEnchantments::isDisabled).orElse(false);
    }

    public static Stream<Holder<Enchantment>> withoutDisabled(Stream<Holder<Enchantment>> candidates)
    {
        return candidates.filter(holder -> !isDisabled(holder));
    }
}
