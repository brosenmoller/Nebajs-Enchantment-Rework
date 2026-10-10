package com.nebaj.nebajsenchantmentrework.component;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.UUID;

public final class ModDataComponents
{
    // Only identifies a Surging pickaxe; its charge lives on the player because rewriting a held item's components restarts the client's block breaking
    public static final DataComponentType<UUID> SURGING_ID = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        NebajSEnchantmentRework.id("surging_id"),
        DataComponentType.<UUID>builder()
            .persistent(UUIDUtil.CODEC)
            .networkSynchronized(UUIDUtil.STREAM_CODEC)
            .build()
    );

    private ModDataComponents() {}

    public static void initialize() {}
}
