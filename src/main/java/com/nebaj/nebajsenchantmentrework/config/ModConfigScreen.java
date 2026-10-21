package com.nebaj.nebajsenchantmentrework.config;

import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import com.nebaj.nebajsenchantmentrework.network.OverhauledEnchantingTableNetworking;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.enchantment.Enchantments;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class ModConfigScreen
{
    private static final String KEY_PREFIX = "gui.nebajs-enchantment-rework.config.";
    private static final int MAX_TOOLTIP_WIDTH = 250;

    private ModConfigScreen() {}

    public static Screen create(Screen parent)
    {
        boolean serverControlled = ModConfigManager.isServerControlled();
        ModConfig draft = ModConfigManager.copy(ModConfigManager.current());
        ModConfig defaults = new ModConfig();

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable(KEY_PREFIX + "title"))
            .setEditable(!serverControlled)
            .setSavingRunnable(() -> save(draft, serverControlled));
        ConfigEntryBuilder entries = builder.entryBuilder();

        ConfigCategory enchantingTab = builder.getOrCreateCategory(Component.translatable(KEY_PREFIX + "tab.enchanting"));
        ConfigCategory experienceTab = builder.getOrCreateCategory(Component.translatable(KEY_PREFIX + "tab.experience"));
        ConfigCategory enchantmentsTab = builder.getOrCreateCategory(Component.translatable(KEY_PREFIX + "tab.enchantments"));

        if (serverControlled)
        {
            enchantingTab.addEntry(entries.startTextDescription(
                Component.translatable(KEY_PREFIX + "server_controlled").withStyle(ChatFormatting.YELLOW)).build());
        }

        Section table = Section.start(entries, "enchanting_table");
        table.choice("treasure_teaching", TreasureTeaching.class, draft.enchantingTable.treasureTeaching, defaults.enchantingTable.treasureTeaching,
            TreasureTeaching::translationKey, value -> draft.enchantingTable.treasureTeaching = value);
        table.choice("xp_cost_preset", CostPreset.class, draft.enchantingTable.xpCostPreset, defaults.enchantingTable.xpCostPreset,
            CostPreset::translationKey, value -> draft.enchantingTable.xpCostPreset = value);
        table.choice("material_cost_preset", CostPreset.class, draft.enchantingTable.materialCostPreset, defaults.enchantingTable.materialCostPreset,
            CostPreset::translationKey, value -> draft.enchantingTable.materialCostPreset = value);
        table.addTo(enchantingTab);

        Section loot = Section.start(entries, "treasure_loot");
        loot.toggle("add_treasure_books", draft.treasureLoot.addTreasureBooks, defaults.treasureLoot.addTreasureBooks,
            value -> draft.treasureLoot.addTreasureBooks = value);
        loot.percent("book_chance_multiplier", draft.treasureLoot.bookChanceMultiplierPercent, defaults.treasureLoot.bookChanceMultiplierPercent, 500,
            value -> draft.treasureLoot.bookChanceMultiplierPercent = value);
        loot.toggle("exclude_restricted_from_random_loot", draft.treasureLoot.excludeRestrictedFromRandomLoot, defaults.treasureLoot.excludeRestrictedFromRandomLoot,
            value -> draft.treasureLoot.excludeRestrictedFromRandomLoot = value);
        loot.addTo(enchantingTab);

        Section behaviour = Section.start(entries, "enchantment_behaviour");
        behaviour.toggle("maxed_fire_protection_immunity", draft.enchantmentBehaviour.maxedFireProtectionImmunity, defaults.enchantmentBehaviour.maxedFireProtectionImmunity,
            value -> draft.enchantmentBehaviour.maxedFireProtectionImmunity = value);
        behaviour.toggle("mace_wind_burst", draft.enchantmentBehaviour.maceWindBurst, defaults.enchantmentBehaviour.maceWindBurst,
            value -> draft.enchantmentBehaviour.maceWindBurst = value);
        behaviour.number("mace_wind_burst_cooldown", draft.enchantmentBehaviour.maceWindBurstCooldownSeconds, defaults.enchantmentBehaviour.maceWindBurstCooldownSeconds, 0, 600,
            value -> draft.enchantmentBehaviour.maceWindBurstCooldownSeconds = value);
        behaviour.percent("beheading_base_chance", draft.enchantmentBehaviour.beheadingBaseChancePercent, defaults.enchantmentBehaviour.beheadingBaseChancePercent, 100,
            value -> draft.enchantmentBehaviour.beheadingBaseChancePercent = value);
        behaviour.percent("beheading_chance_per_level", draft.enchantmentBehaviour.beheadingChancePerLevelPercent, defaults.enchantmentBehaviour.beheadingChancePerLevelPercent, 100,
            value -> draft.enchantmentBehaviour.beheadingChancePerLevelPercent = value);
        behaviour.addTo(enchantingTab);

        Section misc = Section.start(entries, "misc");
        misc.percent("drowned_trident_drop", draft.misc.drownedTridentDropPercent, defaults.misc.drownedTridentDropPercent, 100,
            value -> draft.misc.drownedTridentDropPercent = value);
        misc.restartToggle("elytra_leather_repair", draft.misc.elytraLeatherRepair, defaults.misc.elytraLeatherRepair,
            value -> draft.misc.elytraLeatherRepair = value);
        misc.restartToggle("trident_prismarine_repair", draft.misc.tridentPrismarineRepair, defaults.misc.tridentPrismarineRepair,
            value -> draft.misc.tridentPrismarineRepair = value);
        misc.restartToggle("netherite_diamond_repair", draft.misc.netheriteDiamondRepair, defaults.misc.netheriteDiamondRepair,
            value -> draft.misc.netheriteDiamondRepair = value);
        misc.addTo(enchantingTab);

        Section experience = Section.start(entries, "experience");
        experience.toggle("flat_xp_curve", draft.experience.flatXpCurve, defaults.experience.flatXpCurve,
            value -> draft.experience.flatXpCurve = value);
        experience.number("xp_per_level", draft.experience.xpPerLevel, defaults.experience.xpPerLevel, 1, 1000,
            value -> draft.experience.xpPerLevel = value);
        experience.toggle("drop_share_on_death", draft.experience.dropShareOnDeath, defaults.experience.dropShareOnDeath,
            value -> draft.experience.dropShareOnDeath = value);
        experience.percent("death_drop", draft.experience.deathDropPercent, defaults.experience.deathDropPercent, 100,
            value -> draft.experience.deathDropPercent = value);
        experience.percent("grindstone_refund", draft.experience.grindstoneRefundPercent, defaults.experience.grindstoneRefundPercent, 100,
            value -> draft.experience.grindstoneRefundPercent = value);
        experience.toggle("fishing_xp", draft.experience.fishingXp, defaults.experience.fishingXp,
            value -> draft.experience.fishingXp = value);
        experience.toggle("berry_harvest_xp", draft.experience.berryHarvestXp, defaults.experience.berryHarvestXp,
            value -> draft.experience.berryHarvestXp = value);
        experience.toggle("crop_harvest_xp", draft.experience.cropHarvestXp, defaults.experience.cropHarvestXp,
            value -> draft.experience.cropHarvestXp = value);
        experience.toggle("amethyst_xp", draft.experience.amethystXp, defaults.experience.amethystXp,
            value -> draft.experience.amethystXp = value);
        experience.addTo(experienceTab);

        Section anvil = Section.start(entries, "anvil");
        anvil.toggle("free_repairs_and_renames", draft.anvil.freeRepairsAndRenames, defaults.anvil.freeRepairsAndRenames,
            value -> draft.anvil.freeRepairsAndRenames = value);
        anvil.toggle("remove_too_expensive_cap", draft.anvil.removeTooExpensiveCap, defaults.anvil.removeTooExpensiveCap,
            value -> draft.anvil.removeTooExpensiveCap = value);
        anvil.percent("damage_chance", draft.anvil.damageChancePercent, defaults.anvil.damageChancePercent, 100,
            value -> draft.anvil.damageChancePercent = value);
        anvil.percent("combine_bonus_durability", draft.anvil.combineBonusDurabilityPercent, defaults.anvil.combineBonusDurabilityPercent, 100,
            value -> draft.anvil.combineBonusDurabilityPercent = value);
        anvil.addTo(experienceTab);

        addEnchantmentToggles(enchantmentsTab, entries, draft);

        return builder.build();
    }

    private static void save(ModConfig draft, boolean serverControlled)
    {
        if (serverControlled) return;

        ModConfigManager.save(draft);

        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) server.execute(() -> OverhauledEnchantingTableNetworking.syncConfigToAll(server));
    }

    private static void addEnchantmentToggles(ConfigCategory category, ConfigEntryBuilder entries, ModConfig draft)
    {
        category.addEntry(entries.startTextDescription(
            Component.translatable(KEY_PREFIX + "enchantments.description").withStyle(ChatFormatting.GRAY)).build());

        List<Identifier> vanilla = enchantmentIdsIn(Enchantments.class);
        List<Identifier> mod = enchantmentIdsIn(ModEnchantments.class).stream()
            .filter(id -> ModEnchantments.isEnabled(ResourceKey.create(Registries.ENCHANTMENT, id)))
            .toList();

        Set<Identifier> known = new HashSet<>(vanilla);
        known.addAll(mod);
        List<Identifier> other = new ArrayList<>();
        if (Minecraft.getInstance().level != null)
        {
            Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).keySet().stream()
                .filter(id -> !known.contains(id))
                .forEach(other::add);
        }

        category.addEntry(enchantmentGroup(entries, "enchantments.mod", mod, draft));
        category.addEntry(enchantmentGroup(entries, "enchantments.vanilla", vanilla, draft));
        if (!other.isEmpty()) category.addEntry(enchantmentGroup(entries, "enchantments.other", other, draft));
    }

    private static AbstractConfigListEntry<?> enchantmentGroup(ConfigEntryBuilder entries, String key, List<Identifier> ids, ModConfig draft)
    {
        SubCategoryBuilder group = entries.startSubCategory(Component.translatable(KEY_PREFIX + key)).setExpanded(true);

        ids.stream()
            .sorted(Comparator.comparing(id -> enchantmentName(id).getString()))
            .forEach(id -> group.add(entries.startBooleanToggle(enchantmentName(id), !draft.isEnchantmentDisabled(id))
                .setDefaultValue(true)
                .setSaveConsumer(enabled -> draft.setEnchantmentEnabled(id, enabled))
                .build()));

        return group.build();
    }

    private static Component enchantmentName(Identifier id)
    {
        return Component.translatable(Util.makeDescriptionId("enchantment", id));
    }

    // Reading the key constants means newly added vanilla or mod enchantments show up here without a list to keep in sync
    private static List<Identifier> enchantmentIdsIn(Class<?> holder)
    {
        List<Identifier> ids = new ArrayList<>();
        for (Field field : holder.getFields())
        {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != ResourceKey.class) continue;

            try
            {
                if (field.get(null) instanceof ResourceKey<?> key && key.isFor(Registries.ENCHANTMENT)) ids.add(key.identifier());
            }
            catch (IllegalAccessException ignored)
            {
            }
        }
        return ids;
    }

    // Cloth draws every tooltip line at full length, so long text is wrapped here; re-run on each hover so it follows window resizes
    private static Optional<Component[]> wrappedTooltip(Component text)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int maxWidth = Math.min(minecraft.getWindow().getGuiScaledWidth() / 2, MAX_TOOLTIP_WIDTH);
        List<FormattedText> lines = minecraft.font.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
        return Optional.of(lines.stream().map(line -> (Component) Component.literal(line.getString())).toArray(Component[]::new));
    }

    private record Section(SubCategoryBuilder group, ConfigEntryBuilder entries, String prefix)
    {
        static Section start(ConfigEntryBuilder entries, String name)
        {
            return new Section(entries.startSubCategory(Component.translatable(KEY_PREFIX + name)).setExpanded(true), entries, name + ".");
        }

        void addTo(ConfigCategory category)
        {
            category.addEntry(group.build());
        }

        void toggle(String option, boolean value, boolean defaultValue, Consumer<Boolean> save)
        {
            group.add(entries.startBooleanToggle(label(option), value)
                .setDefaultValue(defaultValue)
                .setTooltipSupplier(() -> tooltip(option))
                .setSaveConsumer(save)
                .build());
        }

        void restartToggle(String option, boolean value, boolean defaultValue, Consumer<Boolean> save)
        {
            group.add(entries.startBooleanToggle(label(option), value)
                .setDefaultValue(defaultValue)
                .setTooltipSupplier(() -> tooltip(option))
                .setSaveConsumer(save)
                .requireRestart()
                .build());
        }

        void percent(String option, int value, int defaultValue, int max, Consumer<Integer> save)
        {
            group.add(entries.startIntSlider(label(option), value, 0, max)
                .setDefaultValue(defaultValue)
                .setTextGetter(percent -> Component.translatable(KEY_PREFIX + "percent", percent))
                .setTooltipSupplier(() -> tooltip(option))
                .setSaveConsumer(save)
                .build());
        }

        void number(String option, int value, int defaultValue, int min, int max, Consumer<Integer> save)
        {
            group.add(entries.startIntField(label(option), value)
                .setDefaultValue(defaultValue)
                .setMin(min)
                .setMax(max)
                .setTooltipSupplier(() -> tooltip(option))
                .setSaveConsumer(save)
                .build());
        }

        <T extends Enum<T>> void choice(String option, Class<T> type, T value, T defaultValue, Function<T, String> nameKey, Consumer<T> save)
        {
            group.add(entries.startEnumSelector(label(option), type, value)
                .setDefaultValue(defaultValue)
                .setEnumNameProvider(choice -> Component.translatable(nameKey.apply(type.cast(choice))))
                .setTooltipSupplier(() -> tooltip(option))
                .setSaveConsumer(save)
                .build());
        }

        private Component label(String option)
        {
            return Component.translatable(KEY_PREFIX + prefix + option);
        }

        private Optional<Component[]> tooltip(String option)
        {
            return wrappedTooltip(Component.translatable(KEY_PREFIX + prefix + option + ".tooltip"));
        }
    }
}
