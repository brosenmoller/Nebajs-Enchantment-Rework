package com.nebaj.nebajsenchantmentrework.block;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ModBlocks
{
    public static final Block CRUSTED_BASALT = register("crusted_basalt", CrustedBasaltBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).randomTicks().noLootTable());

    private ModBlocks() {}

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties)
    {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, NebajSEnchantmentRework.id(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
    }

    public static void initialize() {}
}
