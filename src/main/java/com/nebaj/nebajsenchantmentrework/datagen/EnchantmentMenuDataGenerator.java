package com.nebaj.nebajsenchantmentrework.datagen;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryConfig;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentEntryDataLoader;
import com.nebaj.nebajsenchantmentrework.data.EnchantmentLevelCost;
import com.nebaj.nebajsenchantmentrework.data.MaterialRule;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
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
        if (!ModEnchantments.isEnabled(key)) return;

        entries.put(key.identifier(), config);
    }

    private void register(ResourceKey<Enchantment> key, int order, EnchantmentLevelCost... levels)
    {
        register(key, order, Optional.empty(), levels);
    }

    private void register(ResourceKey<Enchantment> key, int order, List<MaterialRule> materials, EnchantmentLevelCost... levels)
    {
        register(key, order, Optional.of(materials), levels);
    }

    private void register(ResourceKey<Enchantment> key, int order, Optional<List<MaterialRule>> materials, EnchantmentLevelCost... levels)
    {
        register(key, new EnchantmentEntryConfig(order, List.of(levels), Optional.empty(), materials));
    }

    private void registerTreasure(ResourceKey<Enchantment> key, int order, EnchantmentLevelCost... levels)
    {
        registerTreasure(key, order, Optional.empty(), levels);
    }

    private void registerTreasure(ResourceKey<Enchantment> key, int order, List<MaterialRule> materials, EnchantmentLevelCost... levels)
    {
        registerTreasure(key, order, Optional.of(materials), levels);
    }

    private void registerTreasure(ResourceKey<Enchantment> key, int order, Optional<List<MaterialRule>> materials, EnchantmentLevelCost... levels)
    {
        String clue = "enchantment." + NebajSEnchantmentRework.MOD_ID + "." + key.identifier().getPath() + ".clue";
        register(key, new EnchantmentEntryConfig(order, List.of(levels), Optional.of(clue), materials));
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
            Optional.empty(),
            Optional.of(List.of(MaterialRule.FALLBACK))
        ));

        register(Enchantments.UNBREAKING, 0, List.of(MaterialRule.repairMaterial()),
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 6),
            new EnchantmentLevelCost(15, 12, 12)
        );

        registerTreasure(ModEnchantments.TEMPERING, 4,
            new EnchantmentLevelCost(10, 10, 5),
            new EnchantmentLevelCost(30, 15, 10),
            new EnchantmentLevelCost(60, 25, 15)
        );

        register(Enchantments.EFFICIENCY, 5,
            new EnchantmentLevelCost(2, 3, 0),
            new EnchantmentLevelCost(10, 5, 2),
            new EnchantmentLevelCost(20, 10, 6),
            new EnchantmentLevelCost(30, 20, 10),
            new EnchantmentLevelCost(40, 40, 15)
        );

        register(Enchantments.SHARPNESS, 10,
            new EnchantmentLevelCost(2, 2, 0),
            new EnchantmentLevelCost(10, 4, 2),
            new EnchantmentLevelCost(20, 7, 6),
            new EnchantmentLevelCost(30, 12, 10),
            new EnchantmentLevelCost(40, 20, 15)
        );

        register(Enchantments.PROTECTION, 10, List.of(MaterialRule.repairMaterial()),
            new EnchantmentLevelCost(2, 2, 0),
            new EnchantmentLevelCost(10, 4, 2),
            new EnchantmentLevelCost(20, 7, 6),
            new EnchantmentLevelCost(30, 12, 10)
        );

        register(Enchantments.AQUA_AFFINITY, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.HEART_OF_THE_SEA)),
            new EnchantmentLevelCost(10, 1, 20)
        );

        register(Enchantments.BANE_OF_ARTHROPODS, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(15, 15, 6),
            new EnchantmentLevelCost(20, 20, 10),
            new EnchantmentLevelCost(25, 25, 20)
        );

        register(Enchantments.BLAST_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 20, 10)
        );

        register(Enchantments.BREACH, 20,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 30, 10)
        );

        registerTreasure(Enchantments.CHANNELING, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.COPPER_INGOT)),
            new EnchantmentLevelCost(30, 50, 20)
        );

        register(Enchantments.DENSITY, 10,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        registerTreasure(Enchantments.DEPTH_STRIDER, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 5),
            new EnchantmentLevelCost(20, 22, 15)
        );

        register(Enchantments.FEATHER_FALLING, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.FEATHER)),
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 15),
            new EnchantmentLevelCost(30, 40, 20)
        );

        register(Enchantments.FIRE_ASPECT, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.FIRE_CHARGE)),
            new EnchantmentLevelCost(10, 10, 10),
            new EnchantmentLevelCost(20, 20, 15)
        );

        register(Enchantments.FIRE_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10)
        );

        registerTreasure(Enchantments.FLAME, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.FIRE_CHARGE)),
            new EnchantmentLevelCost(15, 15, 15)
        );

        register(Enchantments.FORTUNE, 20,
            new EnchantmentLevelCost(10, 20, 5),
            new EnchantmentLevelCost(15, 40, 10),
            new EnchantmentLevelCost(30, 64, 15)
        );

        registerTreasure(Enchantments.FROST_WALKER, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.BLUE_ICE)),
            new EnchantmentLevelCost(2, 5, 10),
            new EnchantmentLevelCost(10, 7, 15)
        );

        register(Enchantments.IMPALING, 10, List.of(MaterialRule.repairMaterial(), MaterialRule.FALLBACK),
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        registerTreasure(Enchantments.INFINITY, UNSORTED_ORDER,
            new EnchantmentLevelCost(30, 20, 15)
        );

        register(Enchantments.KNOCKBACK, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 4),
            new EnchantmentLevelCost(10, 7, 8)
        );

        register(Enchantments.LOOTING, 30,
            new EnchantmentLevelCost(5, 10, 5),
            new EnchantmentLevelCost(10, 25, 10),
            new EnchantmentLevelCost(20, 40, 15)
        );

        register(Enchantments.LOYALTY, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 6),
            new EnchantmentLevelCost(20, 22, 12)
        );

        registerTreasure(Enchantments.LUCK_OF_THE_SEA, UNSORTED_ORDER,
            new EnchantmentLevelCost(5, 6, 12),
            new EnchantmentLevelCost(15, 11, 16),
            new EnchantmentLevelCost(25, 20, 20)
        );

        register(Enchantments.LUNGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 5),
            new EnchantmentLevelCost(15, 7, 12),
            new EnchantmentLevelCost(30, 22, 20)
        );

        register(Enchantments.LURE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 5),
            new EnchantmentLevelCost(15, 7, 12),
            new EnchantmentLevelCost(30, 22, 20)
        );

        registerTreasure(Enchantments.MULTISHOT, 30,
            new EnchantmentLevelCost(30, 20, 15)
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
            new EnchantmentLevelCost(10, 7, 5),
            new EnchantmentLevelCost(20, 22, 10),
            new EnchantmentLevelCost(30, 40, 15)
        );

        register(Enchantments.PUNCH, 20,
            new EnchantmentLevelCost(20, 8, 10),
            new EnchantmentLevelCost(32, 18, 20)
        );

        register(Enchantments.QUICK_CHARGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 8),
            new EnchantmentLevelCost(20, 22, 18)
        );

        registerTreasure(Enchantments.RESPIRATION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        register(Enchantments.RIPTIDE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 5)
        );

        register(Enchantments.SILK_TOUCH, 30,
            new EnchantmentLevelCost(20, 20, 15)
        );

        register(Enchantments.SMITE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        registerTreasure(Enchantments.SOUL_SPEED, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 5),
            new EnchantmentLevelCost(10, 7, 10),
            new EnchantmentLevelCost(20, 22, 15)
        );

        register(Enchantments.SWEEPING_EDGE, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        registerTreasure(Enchantments.SWIFT_SNEAK, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.ECHO_SHARD)),
            new EnchantmentLevelCost(5, 5, 5),
            new EnchantmentLevelCost(15, 10, 8),
            new EnchantmentLevelCost(30, 20, 16)
        );

        registerTreasure(Enchantments.THORNS, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.PRISMARINE_SHARD)),
            new EnchantmentLevelCost(10, 10, 5),
            new EnchantmentLevelCost(20, 20, 10),
            new EnchantmentLevelCost(30, 30, 15)
        );

        registerTreasure(Enchantments.WIND_BURST, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.WIND_CHARGE)),
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 20, 8),
            new EnchantmentLevelCost(20, 40, 20)
        );

        registerTreasure(Enchantments.MENDING, UNSORTED_ORDER, List.of(MaterialRule.repairMaterial(), MaterialRule.FALLBACK),
            new EnchantmentLevelCost(70, 15)
        );

        registerTreasure(ModEnchantments.BEHEADING, UNSORTED_ORDER,
            new EnchantmentLevelCost(5, 1, 5),
            new EnchantmentLevelCost(10, 2, 10),
            new EnchantmentLevelCost(15, 3, 15)
        );

        registerTreasure(ModEnchantments.KEEPING, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.ECHO_SHARD)),
            new EnchantmentLevelCost(5, 5, 5),
            new EnchantmentLevelCost(5, 5, 10),
            new EnchantmentLevelCost(5, 5, 15)
        );

        registerTreasure(ModEnchantments.MAGMA_STRIDER, UNSORTED_ORDER,
            new EnchantmentLevelCost(5, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );

        registerTreasure(ModEnchantments.CRUST_WALKER, UNSORTED_ORDER, List.of(MaterialRule.fixed(Items.BASALT)),
            new EnchantmentLevelCost(20, 32, 15),
            new EnchantmentLevelCost(30, 64, 20)
        );

        register(ModEnchantments.MELEE_PROTECTION, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 5),
            new EnchantmentLevelCost(20, 22, 10),
            new EnchantmentLevelCost(30, 40, 20)
        );

        registerTreasure(ModEnchantments.MOMENTUM, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        registerTreasure(ModEnchantments.FLURRY, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6),
            new EnchantmentLevelCost(30, 40, 10),
            new EnchantmentLevelCost(40, 64, 20)
        );

        registerTreasure(ModEnchantments.AIR_AFFINITY, UNSORTED_ORDER,
            new EnchantmentLevelCost(12, 10, 20)
        );

        registerTreasure(ModEnchantments.SURGING, UNSORTED_ORDER,
            new EnchantmentLevelCost(2, 5, 0),
            new EnchantmentLevelCost(10, 7, 2),
            new EnchantmentLevelCost(20, 22, 6)
        );
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
