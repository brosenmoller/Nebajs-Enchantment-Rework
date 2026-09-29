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
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnchantmentMenuDataGenerator implements DataProvider
{
    private static final int UNSORTED_ORDER = 100;

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

    private void register(ResourceKey<Enchantment> key, int order, EnchantmentLevelCost... levels)
    {
        register(key, new EnchantmentEntryConfig(order, List.of(levels), Optional.empty()));
    }

    private void registerDefault(EnchantmentEntryConfig config)
    {
        entries.put(EnchantmentEntryDataLoader.DEFAULT_ID, config);
    }

    // TEMPORARY: costs are seeded from the default ladder and orders beyond the top few are placeholders, not balanced yet
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

        register(Enchantments.UNBREAKING, 0,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(ModEnchantments.TEMPERING, 4,
            new EnchantmentLevelCost(5, 10, 5),
            new EnchantmentLevelCost(40, 15, 10),
            new EnchantmentLevelCost(80, 25, 15)
        );

        register(Enchantments.EFFICIENCY, 5,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.SHARPNESS, 10,
            new EnchantmentLevelCost(1, 1),
            new EnchantmentLevelCost(2, 1),
            new EnchantmentLevelCost(3, 1),
            new EnchantmentLevelCost(4, 2),
            new EnchantmentLevelCost(5, 2)
        );

        register(Enchantments.PROTECTION, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.AQUA_AFFINITY, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.BANE_OF_ARTHROPODS, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.BLAST_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.BREACH, 20,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.CHANNELING, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.DENSITY, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.DEPTH_STRIDER, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.FEATHER_FALLING, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.FIRE_ASPECT, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2)
        );

        register(Enchantments.FIRE_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.FLAME, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.FORTUNE, 20,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.FROST_WALKER, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2)
        );

        register(Enchantments.IMPALING, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.INFINITY, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.KNOCKBACK, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2)
        );

        register(Enchantments.LOOTING, 30,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.LOYALTY, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.LUCK_OF_THE_SEA, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.LUNGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.LURE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.MULTISHOT, 30,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.PIERCING, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.POWER, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.PROJECTILE_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        register(Enchantments.PUNCH, 20,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2)
        );

        register(Enchantments.QUICK_CHARGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.RESPIRATION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.RIPTIDE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.SILK_TOUCH, 30,
            new EnchantmentLevelCost(2, 5, 0)
        );

        register(Enchantments.SMITE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        register(Enchantments.SOUL_SPEED, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.SWEEPING_EDGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.SWIFT_SNEAK, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.THORNS, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.WIND_BURST, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.MENDING, UNSORTED_ORDER,
            new EnchantmentLevelCost(45, 50)
        );

        register(ModEnchantments.BEHEADING, new EnchantmentEntryConfig(
            UNSORTED_ORDER,
            List.of(
                new EnchantmentLevelCost(5, 1, 5),
                new EnchantmentLevelCost(10, 2, 10),
                new EnchantmentLevelCost(15, 3, 15)
            ),
            Optional.of("enchantment.nebajs-enchantment-rework.beheading.clue")
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
