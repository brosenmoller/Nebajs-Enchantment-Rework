package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public record XpSettings(
    IntProvider fishingXp,
    IntProvider berryHarvestXp,
    Map<Block, IntProvider> blockBreakXp
)
{
    public static final XpSettings FALLBACK = new XpSettings(UniformInt.of(3, 10), UniformInt.of(0, 1), Map.of());

    public static final Codec<XpSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IntProviders.NON_NEGATIVE_CODEC.fieldOf("fishing_xp").forGetter(XpSettings::fishingXp),
        IntProviders.NON_NEGATIVE_CODEC.fieldOf("berry_harvest_xp").forGetter(XpSettings::berryHarvestXp),
        Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), IntProviders.NON_NEGATIVE_CODEC)
            .optionalFieldOf("block_break_xp", Map.of()).forGetter(XpSettings::blockBreakXp)
    ).apply(instance, XpSettings::new));
}
