package com.nebaj.nebajsenchantmentrework.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Mth;

import java.util.Locale;

public enum CostPreset
{
    @SerializedName("free") FREE(0.0f),
    @SerializedName("very_cheap") VERY_CHEAP(0.25f),
    @SerializedName("cheap") CHEAP(0.5f),
    @SerializedName("normal") NORMAL(1.0f),
    @SerializedName("expensive") EXPENSIVE(1.5f),
    @SerializedName("very_expensive") VERY_EXPENSIVE(2.0f);

    private final float multiplier;

    CostPreset(float multiplier)
    {
        this.multiplier = multiplier;
    }

    public int apply(int baseCost)
    {
        if (multiplier <= 0.0f || baseCost <= 0) return 0;
        return Math.max(1, Mth.ceil(baseCost * multiplier));
    }

    public String translationKey()
    {
        return "gui.nebajs-enchantment-rework.config.cost_preset." + name().toLowerCase(Locale.ROOT);
    }
}
