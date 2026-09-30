package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.data.XpSettingsLoader;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        PayloadTypeRegistry.clientboundPlay().register(
            SyncXpSettingsPayload.TYPE,
            SyncXpSettingsPayload.STREAM_CODEC
        );

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
        {
            if (!ServerPlayNetworking.canSend(player, SyncXpSettingsPayload.TYPE)) return;

            ServerPlayNetworking.send(player, new SyncXpSettingsPayload(XpSettingsLoader.INSTANCE.current().xpPerLevel()));
        });
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

        ClientPlayNetworking.registerGlobalReceiver(SyncXpSettingsPayload.TYPE, (payload, context) ->
            XpSettingsLoader.INSTANCE.setClientXpPerLevel(payload.xpPerLevel()));
    }
}
