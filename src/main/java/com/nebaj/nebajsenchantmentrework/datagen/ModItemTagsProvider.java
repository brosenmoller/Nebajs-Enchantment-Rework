package com.nebaj.nebajsenchantmentrework.datagen;

import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends FabricTagsProvider.ItemTagsProvider
{
    public ModItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture)
    {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        builder(ModTags.Items.BEHEADING_ENCHANTABLE)
            .addOptionalTag(ItemTags.SWORDS)
            .addOptionalTag(ItemTags.AXES);

        builder(ModTags.Items.LOOTING_ENCHANTABLE)
            .addOptionalTag(ItemTags.MELEE_WEAPON_ENCHANTABLE)
            .addOptionalTag(ItemTags.AXES)
            .add(ItemIds.TRIDENT)
            .add(ItemIds.MACE);

        builder(ModTags.Items.FIRE_ASPECT_ENCHANTABLE)
            .addOptionalTag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
            .addOptionalTag(ItemTags.AXES);

        builder(ModTags.Items.SHARPNESS_ENCHANTABLE)
            .addOptionalTag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
            .add(ItemIds.TRIDENT);

        builder(ModTags.Items.BOW_OR_CROSSBOW_ENCHANTABLE)
            .addOptionalTag(ItemTags.BOW_ENCHANTABLE)
            .addOptionalTag(ItemTags.CROSSBOW_ENCHANTABLE);
    }
}
