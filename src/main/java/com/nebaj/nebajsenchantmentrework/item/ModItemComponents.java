package com.nebaj.nebajsenchantmentrework.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Repairable;

public final class ModItemComponents
{
    private ModItemComponents() {}

    public static void initialize()
    {
        DefaultItemComponentEvents.MODIFY.register(context ->
        {
            context.modify(Items.ELYTRA, builder -> builder.set(DataComponents.REPAIRABLE, repairableWith(Items.PHANTOM_MEMBRANE, Items.LEATHER)));
            context.modify(Items.TRIDENT, builder -> builder.set(DataComponents.REPAIRABLE, repairableWith(Items.PRISMARINE_SHARD)));
        });
    }

    private static Repairable repairableWith(Item... items)
    {
        return new Repairable(HolderSet.direct(Item::builtInRegistryHolder, items));
    }
}
