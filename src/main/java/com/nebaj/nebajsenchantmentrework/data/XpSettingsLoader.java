package com.nebaj.nebajsenchantmentrework.data;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class XpSettingsLoader extends SimpleJsonResourceReloadListener<XpSettings>
{
    public static final String FOLDER = "xp_settings";
    public static final Identifier SETTINGS_ID = NebajSEnchantmentRework.id("xp_settings");
    public static final XpSettingsLoader INSTANCE = new XpSettingsLoader();

    private XpSettings current = XpSettings.FALLBACK;

    private XpSettingsLoader()
    {
        super(XpSettings.CODEC, FileToIdConverter.json(FOLDER));
    }

    @Override
    protected void apply(Map<Identifier, XpSettings> prepared, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        XpSettings loaded = prepared.get(SETTINGS_ID);
        if (loaded == null)
        {
            NebajSEnchantmentRework.LOGGER.warn("No xp_settings entry for {}; using built-in defaults", SETTINGS_ID);
            loaded = XpSettings.FALLBACK;
        }

        this.current = loaded;
    }

    @Override
    public String getName()
    {
        return "XP Settings";
    }

    public XpSettings current()
    {
        return current;
    }
}
