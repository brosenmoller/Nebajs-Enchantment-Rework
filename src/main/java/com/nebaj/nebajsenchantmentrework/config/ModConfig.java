package com.nebaj.nebajsenchantmentrework.config;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Mutable on purpose: the config screen writes each field from its own save callback, and Gson keeps the field initializers as defaults for keys missing from the file
public final class ModConfig
{
    public EnchantingTable enchantingTable = new EnchantingTable();
    public Anvil anvil = new Anvil();
    public Experience experience = new Experience();
    public TreasureLoot treasureLoot = new TreasureLoot();
    public EnchantmentBehaviour enchantmentBehaviour = new EnchantmentBehaviour();
    public Misc misc = new Misc();
    public List<String> disabledEnchantments = new ArrayList<>();

    private transient Set<Identifier> disabledEnchantmentIds = Set.of();

    public static final class EnchantingTable
    {
        public TreasureTeaching treasureTeaching = TreasureTeaching.TAUGHT;
        public CostPreset xpCostPreset = CostPreset.NORMAL;
        public CostPreset materialCostPreset = CostPreset.NORMAL;
    }

    public static final class Anvil
    {
        public boolean freeRepairsAndRenames = true;
        public boolean removeTooExpensiveCap = true;
        public int damageChancePercent = 12;
        public int combineBonusDurabilityPercent = 12;
    }

    public static final class Experience
    {
        public boolean flatXpCurve = true;
        public int xpPerLevel = 20;
        public boolean dropShareOnDeath = true;
        public int deathDropPercent = 50;
        public int grindstoneRefundPercent = 50;
        public boolean fishingXp = true;
        public boolean berryHarvestXp = true;
        public boolean cropHarvestXp = true;
        public boolean amethystXp = true;
    }

    public static final class TreasureLoot
    {
        public boolean addTreasureBooks = true;
        public int bookChanceMultiplierPercent = 100;
        public boolean excludeRestrictedFromRandomLoot = true;
    }

    public static final class EnchantmentBehaviour
    {
        public boolean maxedFireProtectionImmunity = true;
        public boolean maceWindBurst = true;
        public int maceWindBurstCooldownSeconds = 4;
        public int beheadingBaseChancePercent = 40;
        public int beheadingChancePerLevelPercent = 20;
    }

    public static final class Misc
    {
        public int drownedTridentDropPercent = 25;
        public boolean elytraLeatherRepair = true;
        public boolean tridentPrismarineRepair = true;
        public boolean netheriteDiamondRepair = true;
    }

    public boolean isEnchantmentDisabled(Identifier id)
    {
        return disabledEnchantmentIds.contains(id);
    }

    public void setEnchantmentEnabled(Identifier id, boolean enabled)
    {
        String idString = id.toString();
        disabledEnchantments.remove(idString);
        if (!enabled) disabledEnchantments.add(idString);
    }

    // Fills in anything a hand-edited or older file left null or out of range
    public ModConfig sanitize()
    {
        if (enchantingTable == null) enchantingTable = new EnchantingTable();
        if (anvil == null) anvil = new Anvil();
        if (experience == null) experience = new Experience();
        if (treasureLoot == null) treasureLoot = new TreasureLoot();
        if (enchantmentBehaviour == null) enchantmentBehaviour = new EnchantmentBehaviour();
        if (misc == null) misc = new Misc();
        if (disabledEnchantments == null) disabledEnchantments = new ArrayList<>();

        if (enchantingTable.treasureTeaching == null) enchantingTable.treasureTeaching = TreasureTeaching.TAUGHT;
        if (enchantingTable.xpCostPreset == null) enchantingTable.xpCostPreset = CostPreset.NORMAL;
        if (enchantingTable.materialCostPreset == null) enchantingTable.materialCostPreset = CostPreset.NORMAL;

        anvil.damageChancePercent = Mth.clamp(anvil.damageChancePercent, 0, 100);
        anvil.combineBonusDurabilityPercent = Mth.clamp(anvil.combineBonusDurabilityPercent, 0, 100);

        experience.xpPerLevel = Math.max(1, experience.xpPerLevel);
        experience.deathDropPercent = Mth.clamp(experience.deathDropPercent, 0, 100);
        experience.grindstoneRefundPercent = Math.max(0, experience.grindstoneRefundPercent);

        treasureLoot.bookChanceMultiplierPercent = Math.max(0, treasureLoot.bookChanceMultiplierPercent);

        enchantmentBehaviour.maceWindBurstCooldownSeconds = Math.max(0, enchantmentBehaviour.maceWindBurstCooldownSeconds);
        enchantmentBehaviour.beheadingBaseChancePercent = Mth.clamp(enchantmentBehaviour.beheadingBaseChancePercent, 0, 100);
        enchantmentBehaviour.beheadingChancePerLevelPercent = Mth.clamp(enchantmentBehaviour.beheadingChancePerLevelPercent, 0, 100);

        misc.drownedTridentDropPercent = Mth.clamp(misc.drownedTridentDropPercent, 0, 100);

        Set<Identifier> ids = new HashSet<>();
        for (String id : disabledEnchantments)
        {
            Identifier parsed = id == null ? null : Identifier.tryParse(id);
            if (parsed != null) ids.add(parsed);
        }
        disabledEnchantmentIds = Set.copyOf(ids);

        return this;
    }
}
