package com.nebaj.nebajsenchantmentrework.datagen;

import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryConfig;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryDataLoader;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentLevelCost;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnchantmentMenuDataGenerator implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final Map<Identifier, EnchantmentEntryConfig> entries = new LinkedHashMap<>();

    public EnchantmentMenuDataGenerator(FabricPackOutput output)
    {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, EnchantmentEntryDataLoader.FOLDER);
        bootstrap();
    }

    private void register(ResourceKey<Enchantment> key, EnchantmentEntryConfig config)
    {
        entries.put(key.identifier(), config);
    }

    private void registerDefault(EnchantmentEntryConfig config)
    {
        entries.put(EnchantmentEntryDataLoader.DEFAULT_ID, config);
    }

    private void bootstrap()
    {
        registerDefault(new EnchantmentEntryConfig(
            Integer.MAX_VALUE,
            List.of(
                new EnchantmentLevelCost(2, 5, 0),
                new EnchantmentLevelCost(10, 7, 2),
                new EnchantmentLevelCost(20, 22, 6),
                new EnchantmentLevelCost(30, 40, 10),
                new EnchantmentLevelCost(40, 64, 20)
            ),
            Optional.empty()
        ));

        register(ModEnchantments.BEHEADING, new EnchantmentEntryConfig(
            0,
            List.of(
                    new EnchantmentLevelCost(5, 1, 5),
                    new EnchantmentLevelCost(10, 2, 10),
                    new EnchantmentLevelCost(15, 3, 15)
            ),
            Optional.of("enchantment.nebajs-enchantment-rework.beheading.clue")
        ));

        register(Enchantments.MENDING, new EnchantmentEntryConfig(
            1,
            List.of(new EnchantmentLevelCost(10, 10)),
            Optional.empty()
        ));

        register(Enchantments.SHARPNESS, new EnchantmentEntryConfig(
            2,
            List.of(
                new EnchantmentLevelCost(1, 1),
                new EnchantmentLevelCost(2, 1),
                new EnchantmentLevelCost(3, 1),
                new EnchantmentLevelCost(4, 2),
                new EnchantmentLevelCost(5, 2)
            ),
            Optional.empty()
        ));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput)
    {
        return DataProvider.saveAll(cachedOutput, EnchantmentEntryConfig.CODEC, pathProvider, entries);
    }

    @Override
    public String getName()
    {
        return "Enchantment Menu Data";
    }
}
