package com.nebaj.nebajsenchantmentrework.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider
{
    public ModEntityTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture)
    {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        builder(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)
            .add(key(EntityTypes.CREEPER))
            .add(key(EntityTypes.SLIME))
            .add(key(EntityTypes.MAGMA_CUBE))
            .add(key(EntityTypes.BLAZE))
            .add(key(EntityTypes.BREEZE))
            .add(key(EntityTypes.GHAST))
            .add(key(EntityTypes.ENDERMAN))
            .add(key(EntityTypes.HOGLIN))
            .add(key(EntityTypes.RAVAGER))
            .add(key(EntityTypes.VEX))
            .add(key(EntityTypes.SHULKER))
            .add(key(EntityTypes.WARDEN))
            .add(key(EntityTypes.CREAKING));
    }

    private static ResourceKey<EntityType<?>> key(EntityType<?> type)
    {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
    }
}
