package com.nebaj.nebajsenchantmentrework.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfigManager
{
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(NebajSEnchantmentRework.MOD_ID + ".json");
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    private static volatile ModConfig local = new ModConfig().sanitize();
    private static volatile ModConfig synced = null;

    private ModConfigManager() {}

    // The server's values while connected to a remote server, otherwise this machine's own file (also what an integrated server uses)
    public static ModConfig current()
    {
        ModConfig serverConfig = synced;
        return serverConfig != null ? serverConfig : local;
    }

    public static boolean isServerControlled()
    {
        return synced != null;
    }

    public static void load()
    {
        if (!Files.exists(PATH))
        {
            save(new ModConfig());
            return;
        }

        try
        {
            ModConfig loaded = fromJson(Files.readString(PATH));
            local = loaded;
            write(loaded);
        }
        catch (IOException | JsonParseException e)
        {
            NebajSEnchantmentRework.LOGGER.error("Could not read {}; using defaults", PATH, e);
            local = new ModConfig().sanitize();
        }
    }

    public static void save(ModConfig config)
    {
        local = config.sanitize();
        write(local);
    }

    public static void applySynced(ModConfig config)
    {
        synced = config;
    }

    public static void clearSynced()
    {
        synced = null;
    }

    public static ModConfig copy(ModConfig config)
    {
        return fromJson(toJson(config));
    }

    public static String toJson(ModConfig config)
    {
        return GSON.toJson(config);
    }

    public static ModConfig fromJson(String json)
    {
        ModConfig parsed = GSON.fromJson(json, ModConfig.class);
        return (parsed == null ? new ModConfig() : parsed).sanitize();
    }

    private static void write(ModConfig config)
    {
        try
        {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, toJson(config));
        }
        catch (IOException e)
        {
            NebajSEnchantmentRework.LOGGER.error("Could not write {}", PATH, e);
        }
    }
}
