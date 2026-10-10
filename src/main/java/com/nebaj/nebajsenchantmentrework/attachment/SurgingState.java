package com.nebaj.nebajsenchantmentrework.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SurgingState(float charge, boolean coolingDown, long stamp, int recoveryTicks)
{
    public static final Codec<SurgingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("charge").forGetter(SurgingState::charge),
        Codec.BOOL.fieldOf("cooling_down").forGetter(SurgingState::coolingDown),
        Codec.LONG.fieldOf("stamp").forGetter(SurgingState::stamp),
        Codec.INT.fieldOf("recovery_ticks").forGetter(SurgingState::recoveryTicks)
    ).apply(instance, SurgingState::new));

    public static final StreamCodec<ByteBuf, SurgingState> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.FLOAT, SurgingState::charge,
            ByteBufCodecs.BOOL, SurgingState::coolingDown,
            ByteBufCodecs.VAR_LONG, SurgingState::stamp,
            ByteBufCodecs.VAR_INT, SurgingState::recoveryTicks,
            SurgingState::new
        );
}
