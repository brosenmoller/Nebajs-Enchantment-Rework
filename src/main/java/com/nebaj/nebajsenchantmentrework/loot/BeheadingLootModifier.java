package com.nebaj.nebajsenchantmentrework.loot;

import com.nebaj.nebajsenchantmentrework.config.ModConfig;
import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import com.nebaj.nebajsenchantmentrework.enchantment.DisabledEnchantments;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;


public final class BeheadingLootModifier
{
    private static final ResourceKey<LootTable> ZOMBIE_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/zombie"));

    private static final ResourceKey<LootTable> SKELETON_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/skeleton"));

    private static final ResourceKey<LootTable> CREEPER_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/creeper"));

    private static final ResourceKey<LootTable> WITHER_SKELETON_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/wither_skeleton"));

    private static final ResourceKey<LootTable> PIGLIN_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/piglin"));

    private static final ResourceKey<LootTable> ENDER_DRAGON_LOOT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/ender_dragon"));

    private BeheadingLootModifier() {}

    public static void initialize()
    {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) ->
        {
            if (!source.isBuiltin()) return;
            if (DisabledEnchantments.isDisabled(ModEnchantments.BEHEADING)) return;

            if (ZOMBIE_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.ZOMBIE_HEAD, registries);
            else if (SKELETON_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.SKELETON_SKULL, registries);
            else if (CREEPER_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.CREEPER_HEAD, registries);
            else if (WITHER_SKELETON_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.WITHER_SKELETON_SKULL, registries);
            else if (PIGLIN_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.PIGLIN_HEAD, registries);
            else if (ENDER_DRAGON_LOOT_TABLE.equals(key)) addBeheadingPool(tableBuilder, Items.DRAGON_HEAD, registries);
        });
    }

    private static void addBeheadingPool(
            LootTable.Builder tableBuilder,
            net.minecraft.world.item.Item head,
            net.minecraft.core.HolderLookup.Provider registries
    )
    {
        ModConfig.EnchantmentBehaviour behaviour = ModConfigManager.current().enchantmentBehaviour;
        LootPool.Builder pool = LootPool.lootPool()
            .add(LootItem.lootTableItem(head))
            .when(LootItemKilledByPlayerCondition.killedByPlayer())
            .when(
                new LootItemRandomChanceWithEnchantedBonusCondition(
                    0.0F,
                    new LevelBasedValue.Linear(behaviour.beheadingBaseChancePercent / 100.0F, behaviour.beheadingChancePerLevelPercent / 100.0F),
                    registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.BEHEADING)
                )
            );

        tableBuilder.withPool(pool);
    }
}