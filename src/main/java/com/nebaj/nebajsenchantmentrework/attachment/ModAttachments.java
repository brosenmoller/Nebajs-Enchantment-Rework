package com.nebaj.nebajsenchantmentrework.attachment;

import com.mojang.serialization.Codec;
import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ModAttachments
{
    public static final AttachmentType<MomentumState> MOMENTUM = AttachmentRegistry.create(
        NebajSEnchantmentRework.id("momentum"),
        builder -> builder.syncWith(MomentumState.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
    );

    public static final AttachmentType<Long> MOMENTUM_LAST_ACTIVE = AttachmentRegistry.create(NebajSEnchantmentRework.id("momentum_last_active"));

    // Keyed by the pickaxe's ModDataComponents.SURGING_ID; a missing entry means that pickaxe is fully charged
    public static final AttachmentType<Map<UUID, SurgingState>> SURGING = AttachmentRegistry.<Map<UUID, SurgingState>>create(
        NebajSEnchantmentRework.id("surging_charges"),
        builder -> builder
            .persistent(Codec.unboundedMap(UUIDUtil.STRING_CODEC, SurgingState.CODEC))
            .copyOnDeath()
            .syncWith(
                ByteBufCodecs.<ByteBuf, UUID, SurgingState, Map<UUID, SurgingState>>map(HashMap::new, UUIDUtil.STREAM_CODEC, SurgingState.STREAM_CODEC),
                AttachmentSyncPredicate.targetOnly()
            )
    );

    private ModAttachments() {}

    public static void initialize() {}
}
