package com.nebaj.nebajsenchantmentrework.enchantment;

import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.stream.Stream;

public final class LocationRestrictedEnchantments
{
    private LocationRestrictedEnchantments() {}

    public static Stream<Holder<Enchantment>> withoutRestricted(Stream<Holder<Enchantment>> candidates)
    {
        return candidates.filter(holder -> !holder.is(ModTags.Enchantments.LOCATION_RESTRICTED));
    }
}
