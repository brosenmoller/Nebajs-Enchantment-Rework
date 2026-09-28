package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.menu.custom.OverhauledEnchantingMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record SyncEnchantmentEntriesPayload(int containerId, List<OverhauledEnchantingMenu.EnchantmentEntry> entries) implements CustomPacketPayload
{
    public static final Identifier PAYLOAD_ID = NebajSEnchantmentRework.id("sync_enchantment_entries");
    public static final CustomPacketPayload.Type<SyncEnchantmentEntriesPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEnchantmentEntriesPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncEnchantmentEntriesPayload::containerId,
            OverhauledEnchantingMenu.EnchantmentEntry.STREAM_CODEC.apply(ByteBufCodecs.list(256)), SyncEnchantmentEntriesPayload::entries,
            SyncEnchantmentEntriesPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
