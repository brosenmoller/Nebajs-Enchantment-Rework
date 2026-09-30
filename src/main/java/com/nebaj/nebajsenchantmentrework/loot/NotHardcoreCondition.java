package com.nebaj.nebajsenchantmentrework.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record NotHardcoreCondition() implements LootItemCondition
{
    public static final NotHardcoreCondition INSTANCE = new NotHardcoreCondition();
    public static final MapCodec<NotHardcoreCondition> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<NotHardcoreCondition> codec()
    {
        return MAP_CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        return !context.getLevel().getLevelData().isHardcore();
    }

    public static LootItemCondition.Builder notHardcore()
    {
        return () -> INSTANCE;
    }
}
