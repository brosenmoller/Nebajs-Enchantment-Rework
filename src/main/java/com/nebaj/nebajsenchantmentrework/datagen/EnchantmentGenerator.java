package com.nebaj.nebajsenchantmentrework.datagen;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.attribute.ModAttributes;
import com.nebaj.nebajsenchantmentrework.block.ModBlocks;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.TagPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnchantmentGenerator extends FabricDynamicRegistryProvider
{
    public EnchantmentGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries)
    {
        entries.addAll(registries.lookupOrThrow(Registries.ENCHANTMENT));
    }

    @Override
    public String getName()
    {
        return "Enchantments";
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder)
    {
        if (!ModEnchantments.isEnabled(key)) return;

        context.register(key, builder.build(key.identifier()));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context)
    {
        register(context, ModEnchantments.BEHEADING,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.BEHEADING_ENCHANTABLE),
                    10,
                    3,
                    Enchantment.dynamicCost(1, 10),
                    Enchantment.dynamicCost(1, 15),
                    3,
                    EquipmentSlotGroup.HAND
                )
            )
        );

        register(context, ModEnchantments.TEMPERING,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                    10,
                    3,
                    Enchantment.dynamicCost(1, 10),
                    Enchantment.dynamicCost(1, 15),
                    3,
                    EquipmentSlotGroup.ANY
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.TEMPERING_EXCLUSIVE))
        );

        register(context, ModEnchantments.KEEPING,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                    2,
                    3,
                    Enchantment.dynamicCost(1, 10),
                    Enchantment.dynamicCost(1, 15),
                    4,
                    EquipmentSlotGroup.ANY
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.KEEPING_EXCLUSIVE))
        );

        register(context, ModEnchantments.MAGMA_STRIDER,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                    2,
                    3,
                    Enchantment.dynamicCost(10, 10),
                    Enchantment.dynamicCost(25, 10),
                    4,
                    EquipmentSlotGroup.FEET
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.MAGMA_STRIDER_EXCLUSIVE))
            .withEffect(
                EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                    NebajSEnchantmentRework.id("enchantment.magma_strider"),
                    ModAttributes.LAVA_MOVEMENT_EFFICIENCY,
                    LevelBasedValue.perLevel(0.33333334F),
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        );

        register(context, ModEnchantments.CRUST_WALKER,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                    2,
                    2,
                    Enchantment.dynamicCost(10, 10),
                    Enchantment.dynamicCost(25, 10),
                    4,
                    EquipmentSlotGroup.FEET
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.CRUST_WALKER_EXCLUSIVE))
            .withEffect(
                EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                DamageImmunity.INSTANCE,
                DamageSourceCondition.hasDamageSource(
                    DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(context.lookup(Registries.DAMAGE_TYPE), DamageTypeTags.BURN_FROM_STEPPING))
                        .tag(TagPredicate.isNot(context.lookup(Registries.DAMAGE_TYPE), DamageTypeTags.BYPASSES_INVULNERABILITY))
                )
            )
            .withEffect(
                EnchantmentEffectComponents.LOCATION_CHANGED,
                new ReplaceDisk(
                    new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
                    LevelBasedValue.constant(1.0F),
                    new Vec3i(0, -1, 0),
                    Optional.of(
                        BlockPredicate.allOf(
                            BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                            BlockPredicate.matchesBlocks(Blocks.LAVA),
                            BlockPredicate.matchesFluids(Fluids.LAVA),
                            BlockPredicate.unobstructed()
                        )
                    ),
                    BlockStateProvider.holderOf(ModBlocks.CRUSTED_BASALT),
                    Optional.of(GameEvent.BLOCK_PLACE)
                ),
                AllOfCondition.allOf(
                    LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))
                    ),
                    InvertedLootItemCondition.invert(
                        LootItemEntityPropertyCondition.hasProperties(
                            LootContext.EntityTarget.THIS,
                            EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity())
                        )
                    )
                )
            )
        );

        register(context, ModEnchantments.MELEE_PROTECTION,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                    5,
                    4,
                    Enchantment.dynamicCost(3, 6),
                    Enchantment.dynamicCost(9, 6),
                    2,
                    EquipmentSlotGroup.ARMOR
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
            .withEffect(
                EnchantmentEffectComponents.DAMAGE_PROTECTION,
                new AddValue(LevelBasedValue.perLevel(2.0F)),
                DamageSourceCondition.hasDamageSource(
                    DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(context.lookup(Registries.DAMAGE_TYPE), ModTags.DamageTypes.IS_MELEE))
                        .tag(TagPredicate.isNot(context.lookup(Registries.DAMAGE_TYPE), DamageTypeTags.BYPASSES_INVULNERABILITY))
                )
            )
        );

        register(context, ModEnchantments.MOMENTUM,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.MINING_ENCHANTABLE),
                    2,
                    5,
                    Enchantment.dynamicCost(1, 10),
                    Enchantment.dynamicCost(51, 10),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.MINING_SPEED_EXCLUSIVE))
        );

        register(context, ModEnchantments.SURGING,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.MINING_ENCHANTABLE),
                    1,
                    3,
                    Enchantment.dynamicCost(15, 15),
                    Enchantment.dynamicCost(65, 15),
                    8,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.MINING_SPEED_EXCLUSIVE))
        );

        register(context, ModEnchantments.FLURRY,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                    2,
                    5,
                    Enchantment.dynamicCost(5, 8),
                    Enchantment.dynamicCost(25, 8),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
            .withEffect(
                EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                    NebajSEnchantmentRework.id("enchantment.flurry"),
                    Attributes.ATTACK_SPEED,
                    LevelBasedValue.perLevel(0.08F),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            )
        );

        register(context, ModEnchantments.AIR_AFFINITY,
            Enchantment.enchantment(
                Enchantment.definition(
                    context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                    2,
                    1,
                    Enchantment.constantCost(1),
                    Enchantment.constantCost(41),
                    4,
                    EquipmentSlotGroup.HEAD
                )
            )
            .exclusiveWith(context.lookup(Registries.ENCHANTMENT).getOrThrow(ModTags.Enchantments.AIR_AFFINITY_EXCLUSIVE))
            .withEffect(
                EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                    NebajSEnchantmentRework.id("enchantment.air_affinity"),
                    ModAttributes.AIRBORNE_MINING_SPEED,
                    LevelBasedValue.constant(0.8F),
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        );
    }
}
