package com.nebaj.nebajsenchantmentrework.data;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentEntryDataLoader extends SimpleJsonResourceReloadListener<EnchantmentEntryConfig>
{
    public static final String FOLDER = "enchantment_menu_data";
    public static final Identifier DEFAULT_ID = NebajSEnchantmentRework.id("default");
    public static final EnchantmentEntryDataLoader INSTANCE = new EnchantmentEntryDataLoader();

    private Map<Identifier, EnchantmentEntryConfig> loaded = Map.of();
    private final Set<Identifier> warnedMissing = ConcurrentHashMap.newKeySet();
    private final Set<Identifier> warnedMismatch = ConcurrentHashMap.newKeySet();

    private EnchantmentEntryDataLoader()
    {
        super(EnchantmentEntryConfig.CODEC, FileToIdConverter.json(FOLDER));
    }

    @Override
    protected void apply(Map<Identifier, EnchantmentEntryConfig> prepared, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        this.loaded = Map.copyOf(prepared);
        this.warnedMissing.clear();
        this.warnedMismatch.clear();
    }

    @Override
    public String getName()
    {
        return "Enchantment Menu Data";
    }

    public EnchantmentEntryConfig getOrFallback(Identifier enchantmentId, int maxLevel)
    {
        EnchantmentEntryConfig config = loaded.get(enchantmentId);
        if (config != null)
        {
            if (config.levels().size() != maxLevel && warnedMismatch.add(enchantmentId))
            {
                NebajSEnchantmentRework.LOGGER.warn(
                    "enchantment_menu_data for {} has {} level entries but max level is {}; costs will clamp",
                    enchantmentId, config.levels().size(), maxLevel
                );
            }
            return config;
        }

        if (warnedMissing.add(enchantmentId))
        {
            NebajSEnchantmentRework.LOGGER.warn("No enchantment_menu_data entry for {}; using default cost/order", enchantmentId);
        }

        EnchantmentEntryConfig defaultConfig = loaded.get(DEFAULT_ID);
        return defaultConfig != null ? defaultConfig : EnchantmentEntryConfig.FALLBACK;
    }
}
