package com.nebaj.nebajsenchantmentrework.mixin;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerGameMode.class)
public interface ServerPlayerGameModeAccessor
{
    @Accessor("isDestroyingBlock")
    boolean nebajsenchantmentrework$isDestroyingBlock();
}
