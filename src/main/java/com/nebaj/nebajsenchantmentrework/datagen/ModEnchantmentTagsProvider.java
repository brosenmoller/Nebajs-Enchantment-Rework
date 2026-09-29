package com.nebaj.nebajsenchantmentrework.datagen;

import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends FabricTagsProvider<Enchantment>
{
    public ModEnchantmentTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture)
    {
        super(output, Registries.ENCHANTMENT, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        builder(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK)
            .add(Enchantments.MENDING)
            .add(Enchantments.DEPTH_STRIDER)
            .add(Enchantments.AQUA_AFFINITY)
            .add(Enchantments.LUCK_OF_THE_SEA)
            .add(Enchantments.FROST_WALKER)
            .add(Enchantments.SWIFT_SNEAK)
            .add(Enchantments.WIND_BURST)
            .add(Enchantments.SOUL_SPEED)
            .add(Enchantments.MULTISHOT)
            .add(Enchantments.FLAME)
            .add(ModEnchantments.BEHEADING);
    }
}
