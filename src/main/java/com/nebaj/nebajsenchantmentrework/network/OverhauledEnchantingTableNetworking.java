package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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
            SyncModConfigPayload.TYPE,
            SyncModConfigPayload.STREAM_CODEC
        );

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> ModConfigManager.load());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> sendConfig(player));
    }

    public static void syncConfigToAll(MinecraftServer server)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            sendConfig(player);
        }
    }

    private static void sendConfig(ServerPlayer player)
    {
        if (!ServerPlayNetworking.canSend(player, SyncModConfigPayload.TYPE)) return;

        ServerPlayNetworking.send(player, new SyncModConfigPayload(ModConfigManager.toJson(ModConfigManager.current())));
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

        // The integrated server already shares this machine's config, so only a remote server's values are applied
        ClientPlayNetworking.registerGlobalReceiver(SyncModConfigPayload.TYPE, (payload, context) ->
        {
            if (context.client().hasSingleplayerServer()) return;

            ModConfigManager.applySynced(ModConfigManager.fromJson(payload.configJson()));
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ModConfigManager.clearSynced());
    }
}
