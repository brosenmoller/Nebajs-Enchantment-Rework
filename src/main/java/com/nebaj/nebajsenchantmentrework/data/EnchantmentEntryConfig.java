package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Optional;

public record EnchantmentEntryConfig(int order, List<EnchantmentLevelCost> levels, Optional<String> clue)
{
    public static final EnchantmentEntryConfig FALLBACK =
        new EnchantmentEntryConfig(Integer.MAX_VALUE, List.of(EnchantmentLevelCost.FALLBACK), Optional.empty());

    public static final Codec<EnchantmentEntryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("order").forGetter(EnchantmentEntryConfig::order),
        EnchantmentLevelCost.CODEC.listOf().fieldOf("levels").forGetter(EnchantmentEntryConfig::levels),
        Codec.STRING.optionalFieldOf("clue").forGetter(EnchantmentEntryConfig::clue)
    ).apply(instance, EnchantmentEntryConfig::new));

    public EnchantmentLevelCost costForLevel(int level)
    {
        if (levels.isEmpty()) return EnchantmentLevelCost.FALLBACK;
        int index = Mth.clamp(level - 1, 0, levels.size() - 1);
        return levels.get(index);
    }
}
