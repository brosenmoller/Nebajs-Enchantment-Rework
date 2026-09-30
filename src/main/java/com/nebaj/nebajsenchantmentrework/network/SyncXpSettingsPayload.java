package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncXpSettingsPayload(int xpPerLevel) implements CustomPacketPayload
{
    public static final Identifier PAYLOAD_ID = NebajSEnchantmentRework.id("sync_xp_settings");
    public static final CustomPacketPayload.Type<SyncXpSettingsPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncXpSettingsPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncXpSettingsPayload::xpPerLevel,
            SyncXpSettingsPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
