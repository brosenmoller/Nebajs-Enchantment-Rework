package com.nebaj.nebajsenchantmentrework;

import com.nebaj.nebajsenchantmentrework.datagen.EnchantmentMenuDataGenerator;
import com.nebaj.nebajsenchantmentrework.datagen.ModDamageTypeTagsProvider;
import com.nebaj.nebajsenchantmentrework.datagen.ModEnchantmentTagsProvider;
import com.nebaj.nebajsenchantmentrework.datagen.ModEntityTypeTagsProvider;
import com.nebaj.nebajsenchantmentrework.datagen.ModItemTagsProvider;
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
        pack.addProvider(ModDamageTypeTagsProvider::new);
        pack.addProvider(ModItemTagsProvider::new);
        pack.addProvider(ModEntityTypeTagsProvider::new);
        pack.addProvider(EnchantmentMenuDataGenerator::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder)
    {
        registryBuilder.add(Registries.ENCHANTMENT, EnchantmentGenerator::bootstrap);
    }
}
