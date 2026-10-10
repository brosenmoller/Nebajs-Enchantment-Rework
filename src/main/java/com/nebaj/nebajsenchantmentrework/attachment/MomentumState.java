package com.nebaj.nebajsenchantmentrework.attachment;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

public record MomentumState(Block block, int count)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MomentumState> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK), MomentumState::block,
            ByteBufCodecs.VAR_INT, MomentumState::count,
            MomentumState::new
        );
}
