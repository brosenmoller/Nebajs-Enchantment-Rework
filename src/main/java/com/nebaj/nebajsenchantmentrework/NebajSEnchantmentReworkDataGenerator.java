package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.datagen.ModEnchantmentTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import com.nebaj.nebajsenchantmentrework.datagen.EnchantmentGenerator;

public class NebajSEnchantmentReworkDataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(EnchantmentGenerator::new);
        pack.addProvider(ModEnchantmentTagsProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.ENCHANTMENT, EnchantmentGenerator::bootstrap);
    }
}
