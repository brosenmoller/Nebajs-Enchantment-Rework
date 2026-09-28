package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentLevelCost(int xpCost, int materialCost, int requiredBookshelves)
{
    public static final EnchantmentLevelCost FALLBACK = new EnchantmentLevelCost(1, 1);

    public EnchantmentLevelCost(int xpCost, int materialCost)
    {
        this(xpCost, materialCost, 0);
    }

    public static final Codec<EnchantmentLevelCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("xp_cost").forGetter(EnchantmentLevelCost::xpCost),
        Codec.INT.fieldOf("material_cost").forGetter(EnchantmentLevelCost::materialCost),
        Codec.INT.optionalFieldOf("required_bookshelves", 0).forGetter(EnchantmentLevelCost::requiredBookshelves)
    ).apply(instance, EnchantmentLevelCost::new));
}
