package com.nebaj.nebajsenchantmentrework.network;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public record SyncUnlockedEnchantmentsPayload(int containerId, List<Holder<Enchantment>> unlocked) implements CustomPacketPayload
{
    public static final Identifier PAYLOAD_ID = NebajSEnchantmentRework.id("sync_unlocked_enchantments");
    public static final CustomPacketPayload.Type<SyncUnlockedEnchantmentsPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncUnlockedEnchantmentsPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncUnlockedEnchantmentsPayload::containerId,
            Enchantment.STREAM_CODEC.apply(ByteBufCodecs.list(256)), SyncUnlockedEnchantmentsPayload::unlocked,
            SyncUnlockedEnchantmentsPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
