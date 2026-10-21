package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncModConfigPayload(String configJson) implements CustomPacketPayload
{
    public static final Identifier PAYLOAD_ID = NebajSEnchantmentRework.id("sync_config");
    public static final CustomPacketPayload.Type<SyncModConfigPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncModConfigPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.stringUtf8(Integer.MAX_VALUE), SyncModConfigPayload::configJson,
            SyncModConfigPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
