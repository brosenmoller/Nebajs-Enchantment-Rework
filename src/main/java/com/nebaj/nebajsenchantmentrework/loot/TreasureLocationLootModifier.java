package com.nebaj.nebajsenchantmentrework.loot;

import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TreasureLocationLootModifier
{
    private record TreasureLocation(float chance, List<ResourceKey<Enchantment>> enchantments, List<LootItemCondition.Builder> extraConditions)
    {
        TreasureLocation(float chance, List<ResourceKey<Enchantment>> enchantments)
        {
            this(chance, enchantments, List.of());
        }
    }

    private static final Map<ResourceKey<LootTable>, TreasureLocation> LOCATIONS = Map.ofEntries(
        Map.entry(lootTable("chests/underwater_ruin_big"), new TreasureLocation(0.3F, List.of(Enchantments.RESPIRATION))),
        Map.entry(lootTable("chests/underwater_ruin_small"), new TreasureLocation(0.1F, List.of(Enchantments.RESPIRATION))),
        Map.entry(lootTable("chests/buried_treasure"), new TreasureLocation(0.5F, List.of(Enchantments.DEPTH_STRIDER))),
        Map.entry(lootTable("chests/shipwreck_treasure"), new TreasureLocation(0.3F, List.of(Enchantments.LUCK_OF_THE_SEA))),
        Map.entry(lootTable("chests/shipwreck_supply"), new TreasureLocation(0.1F, List.of(Enchantments.LUCK_OF_THE_SEA))),
        Map.entry(lootTable("chests/igloo_chest"), new TreasureLocation(0.5F, List.of(Enchantments.FROST_WALKER))),
        Map.entry(lootTable("chests/village/village_snowy_house"), new TreasureLocation(0.15F, List.of(Enchantments.FROST_WALKER))),
        Map.entry(lootTable("chests/ancient_city"), new TreasureLocation(0.3F, List.of(ModEnchantments.KEEPING), List.of(NotHardcoreCondition.notHardcore()))),
        Map.entry(lootTable("chests/nether_bridge"), new TreasureLocation(0.2F, List.of(ModEnchantments.MAGMA_STRIDER, Enchantments.FLAME))),
        Map.entry(lootTable("chests/ruined_portal"), new TreasureLocation(0.2F, List.of(ModEnchantments.CRUST_WALKER))),
        Map.entry(lootTable("chests/end_city_treasure"), new TreasureLocation(0.2F, List.of(ModEnchantments.BEHEADING, Enchantments.MENDING))),
        Map.entry(lootTable("chests/village/village_armorer"), new TreasureLocation(0.4F, List.of(ModEnchantments.TEMPERING))),
        Map.entry(lootTable("chests/village/village_toolsmith"), new TreasureLocation(0.5F, List.of(ModEnchantments.TEMPERING))),
        Map.entry(lootTable("chests/village/village_weaponsmith"), new TreasureLocation(0.4F, List.of(ModEnchantments.TEMPERING))),
        Map.entry(lootTable("chests/pillager_outpost"), new TreasureLocation(0.6F, List.of(Enchantments.MULTISHOT))),
        Map.entry(lootTable("chests/bastion_treasure"), new TreasureLocation(0.4F, List.of(ModEnchantments.MOMENTUM))),
        Map.entry(lootTable("chests/bastion_other"), new TreasureLocation(0.1F, List.of(ModEnchantments.MOMENTUM))),
        Map.entry(lootTable("chests/desert_pyramid"), new TreasureLocation(0.4F, List.of(ModEnchantments.FLURRY))),
        Map.entry(lootTable("chests/jungle_temple"), new TreasureLocation(0.9F, List.of(ModEnchantments.AIR_AFFINITY))),
        Map.entry(lootTable("chests/woodland_mansion"), new TreasureLocation(0.6F, List.of(ModEnchantments.SURGING, Enchantments.CHANNELING))),
        Map.entry(lootTable("chests/stronghold_library"), new TreasureLocation(0.4F, List.of(Enchantments.INFINITY))),
        Map.entry(lootTable("entities/elder_guardian"), new TreasureLocation(1.0F, List.of(Enchantments.THORNS), List.of(LootItemKilledByPlayerCondition.killedByPlayer())))
    );

    private TreasureLocationLootModifier() {}

    public static void initialize()
    {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) ->
        {
            if (!source.isBuiltin()) return;

            TreasureLocation location = LOCATIONS.get(key);
            if (location == null) return;

            HolderGetter<Enchantment> enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
            List<Holder<Enchantment>> enchantments = location.enchantments().stream()
                .map(enchantmentLookup::get)
                .<Holder<Enchantment>>flatMap(Optional::stream)
                .toList();
            if (enchantments.isEmpty()) return;

            tableBuilder.withPool(createTreasureBookPool(location, enchantments));
        });
    }

    private static LootPool.Builder createTreasureBookPool(TreasureLocation location, List<Holder<Enchantment>> enchantments)
    {
        LootPool.Builder pool = LootPool.lootPool()
            .add(LootItem.lootTableItem(Items.BOOK).apply(new EnchantRandomlyFunction.Builder().withOneOf(HolderSet.direct(enchantments))))
            .when(LootItemRandomChanceCondition.randomChance(location.chance()));

        for (LootItemCondition.Builder condition : location.extraConditions())
        {
            pool.when(condition);
        }

        return pool;
    }

    private static ResourceKey<LootTable> lootTable(String path)
    {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace(path));
    }
}
