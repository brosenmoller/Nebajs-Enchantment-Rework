package com.nebaj.nebajsenchantmentrework.data;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.Map;

public class XpSettingsLoader extends SimpleJsonResourceReloadListener<XpSettings>
{
    public static final String FOLDER = "xp_settings";
    public static final Identifier SETTINGS_ID = NebajSEnchantmentRework.id("xp_settings");
    public static final XpSettingsLoader INSTANCE = new XpSettingsLoader();

    private XpSettings current = XpSettings.FALLBACK;
    private int clientXpPerLevel = XpSettings.FALLBACK.xpPerLevel();

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

    // The client renders the XP bar from getXpNeededForNextLevel too, so it needs the server's value (synced via SyncXpSettingsPayload)
    public int xpPerLevel(Level level)
    {
        return level.isClientSide() ? clientXpPerLevel : current.xpPerLevel();
    }

    public void setClientXpPerLevel(int xpPerLevel)
    {
        this.clientXpPerLevel = xpPerLevel;
    }
}
