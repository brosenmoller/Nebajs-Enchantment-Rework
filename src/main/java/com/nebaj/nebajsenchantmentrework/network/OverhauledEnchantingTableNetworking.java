package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

public final class OverhauledEnchantingTableNetworking
{
    private OverhauledEnchantingTableNetworking() { }

    public static void registerCommon()
    {
        PayloadTypeRegistry.clientboundPlay().register(
            SyncEnchantmentEntriesPayload.TYPE,
            SyncEnchantmentEntriesPayload.STREAM_CODEC
        );
    }

    public static void registerClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(SyncEnchantmentEntriesPayload.TYPE, (payload, context) ->
        {
            Minecraft client = context.client();

            if (client.player != null
                && client.player.containerMenu instanceof OverhauledEnchantingMenu menu
                && menu.containerId == payload.containerId())
            {
                menu.applySyncedEnchantmentEntries(payload.entries());
            }
        });
    }
}
