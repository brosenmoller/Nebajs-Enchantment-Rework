package com.nebaj.nebajsenchantmentrework.config;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public enum TreasureTeaching
{
    @SerializedName("vanilla") VANILLA,
    @SerializedName("taught") TAUGHT,
    @SerializedName("always") ALWAYS;

    public String translationKey()
    {
        return "gui.nebajs-enchantment-rework.config.treasure_teaching." + name().toLowerCase(Locale.ROOT);
    }
}
