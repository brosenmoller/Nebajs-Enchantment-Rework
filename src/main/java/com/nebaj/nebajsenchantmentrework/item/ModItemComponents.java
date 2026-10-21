package com.nebaj.nebajsenchantmentrework.item;

import com.nebaj.nebajsenchantmentrework.config.ModConfig;
import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Repairable;

import java.util.Optional;

public final class ModItemComponents
{
    private ModItemComponents() {}

    public static void initialize()
    {
        ModConfig.Misc misc = ModConfigManager.current().misc;

        DefaultItemComponentEvents.MODIFY.register(context ->
        {
            if (misc.elytraLeatherRepair)
            {
                context.modify(Items.ELYTRA, builder -> builder.set(DataComponents.REPAIRABLE, repairableWith(Items.PHANTOM_MEMBRANE, Items.LEATHER)));
            }
            if (misc.tridentPrismarineRepair)
            {
                context.modify(Items.TRIDENT, builder -> builder.set(DataComponents.REPAIRABLE, repairableWith(Items.PRISMARINE_SHARD)));
            }
            if (misc.netheriteDiamondRepair)
            {
                context.modify(ModItemComponents::isRepairedWithNetherite, (builder, item) -> builder.set(DataComponents.REPAIRABLE, repairableWith(Items.DIAMOND)));
            }
        });
    }

    // Matching the repair tag rather than listing items also catches every other netherite tool and armor piece
    private static boolean isRepairedWithNetherite(Item item)
    {
        Repairable repairable = item.components().get(DataComponents.REPAIRABLE);
        if (repairable == null) return false;

        Optional<TagKey<Item>> tag = repairable.items().unwrapKey();
        return tag.isPresent() && (tag.get().equals(ItemTags.NETHERITE_TOOL_MATERIALS) || tag.get().equals(ItemTags.REPAIRS_NETHERITE_ARMOR));
    }

    private static Repairable repairableWith(Item... items)
    {
        return new Repairable(HolderSet.direct(Item::builtInRegistryHolder, items));
    }
}
