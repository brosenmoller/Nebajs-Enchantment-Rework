package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record ItemMatcher(Either<TagKey<Item>, Holder<Item>> value)
{
    public static final Codec<ItemMatcher> CODEC =
        Codec.either(TagKey.hashedCodec(Registries.ITEM), BuiltInRegistries.ITEM.holderByNameCodec())
            .xmap(ItemMatcher::new, ItemMatcher::value);

    public static ItemMatcher of(Item item)
    {
        return new ItemMatcher(Either.right(BuiltInRegistries.ITEM.wrapAsHolder(item)));
    }

    public static ItemMatcher of(TagKey<Item> tag)
    {
        return new ItemMatcher(Either.left(tag));
    }

    public HolderSet<Item> resolve()
    {
        return value.<HolderSet<Item>>map(BuiltInRegistries.ITEM::getOrThrow, holder -> HolderSet.direct(holder));
    }

    public boolean test(Holder<Item> item)
    {
        return value.map(item::is, holder -> holder.value() == item.value());
    }
}
