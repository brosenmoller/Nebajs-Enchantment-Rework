package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public record EnchantmentEntryConfig(int order, List<EnchantmentLevelCost> levels, Optional<String> clue, Optional<List<MaterialRule>> materials)
{
    public static final EnchantmentEntryConfig FALLBACK =
        new EnchantmentEntryConfig(Integer.MAX_VALUE, List.of(EnchantmentLevelCost.FALLBACK), Optional.empty(), Optional.empty());

    public static final Codec<EnchantmentEntryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("order").forGetter(EnchantmentEntryConfig::order),
        EnchantmentLevelCost.CODEC.listOf().fieldOf("levels").forGetter(EnchantmentEntryConfig::levels),
        Codec.STRING.optionalFieldOf("clue").forGetter(EnchantmentEntryConfig::clue),
        MaterialRule.CODEC.listOf().optionalFieldOf("materials").forGetter(EnchantmentEntryConfig::materials)
    ).apply(instance, EnchantmentEntryConfig::new));

    public EnchantmentLevelCost costForLevel(int level)
    {
        if (levels.isEmpty()) return EnchantmentLevelCost.FALLBACK;
        int index = Mth.clamp(level - 1, 0, levels.size() - 1);
        return levels.get(index);
    }

    public HolderSet<Item> resolveMaterial(ItemStack gear, EnchantmentEntryConfig defaultConfig)
    {
        return firstResolved(materials.orElse(List.of()), gear)
            .or(() -> firstResolved(defaultConfig.materials().orElse(List.of()), gear))
            .orElseGet(() -> MaterialRule.FALLBACK.resolveFor(gear).orElseThrow());
    }

    private static Optional<HolderSet<Item>> firstResolved(List<MaterialRule> rules, ItemStack gear)
    {
        for (MaterialRule rule : rules)
        {
            Optional<HolderSet<Item>> resolved = rule.resolveFor(gear);
            if (resolved.isPresent()) return resolved;
        }
        return Optional.empty();
    }

    public static boolean isLapis(HolderSet<Item> material)
    {
        return material.size() == 1 && material.get(0).value() == Items.LAPIS_LAZULI;
    }
}
