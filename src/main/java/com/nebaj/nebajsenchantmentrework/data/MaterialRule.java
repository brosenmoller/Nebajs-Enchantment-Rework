package com.nebaj.nebajsenchantmentrework.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Repairable;

import java.util.Optional;

public record MaterialRule(Optional<ItemMatcher> appliesTo, Optional<ItemMatcher> madeOf, MaterialCost cost)
{
    public sealed interface MaterialCost permits Fixed, RepairMaterial
    {
        String REPAIR_MATERIAL_KEY = "repair_material";

        Codec<RepairMaterial> REPAIR_MATERIAL_CODEC = Codec.STRING.comapFlatMap(
            string -> string.equals(REPAIR_MATERIAL_KEY)
                ? DataResult.success(RepairMaterial.INSTANCE)
                : DataResult.error(() -> "Not " + REPAIR_MATERIAL_KEY + ": " + string),
            repairMaterial -> REPAIR_MATERIAL_KEY
        );

        Codec<MaterialCost> CODEC = Codec.either(REPAIR_MATERIAL_CODEC, ItemMatcher.CODEC).xmap(
            either -> either.<MaterialCost>map(repairMaterial -> repairMaterial, Fixed::new),
            cost -> cost instanceof Fixed fixed
                ? Either.<RepairMaterial, ItemMatcher>right(fixed.item())
                : Either.<RepairMaterial, ItemMatcher>left(RepairMaterial.INSTANCE)
        );
    }

    public record Fixed(ItemMatcher item) implements MaterialCost { }

    public record RepairMaterial() implements MaterialCost
    {
        public static final RepairMaterial INSTANCE = new RepairMaterial();
    }

    public static final Codec<MaterialRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemMatcher.CODEC.optionalFieldOf("applies_to").forGetter(MaterialRule::appliesTo),
        ItemMatcher.CODEC.optionalFieldOf("made_of").forGetter(MaterialRule::madeOf),
        MaterialCost.CODEC.fieldOf("cost").forGetter(MaterialRule::cost)
    ).apply(instance, MaterialRule::new));

    public static final MaterialRule FALLBACK = fixed(Items.LAPIS_LAZULI);

    public static MaterialRule fixed(Item item)
    {
        return new MaterialRule(Optional.empty(), Optional.empty(), new Fixed(ItemMatcher.of(item)));
    }

    public static MaterialRule fixed(TagKey<Item> tag)
    {
        return new MaterialRule(Optional.empty(), Optional.empty(), new Fixed(ItemMatcher.of(tag)));
    }

    public static MaterialRule repairMaterial()
    {
        return new MaterialRule(Optional.empty(), Optional.empty(), RepairMaterial.INSTANCE);
    }

    public Optional<HolderSet<Item>> resolveFor(ItemStack gear)
    {
        if (!appliesTo.map(matcher -> matcher.test(gear.typeHolder())).orElse(true)) return Optional.empty();

        Optional<HolderSet<Item>> gearMaterial = Optional.ofNullable(gear.get(DataComponents.REPAIRABLE)).map(Repairable::items);
        if (madeOf.isPresent() && !gearMaterial.map(items -> items.stream().anyMatch(madeOf.get()::test)).orElse(false)) return Optional.empty();

        Optional<HolderSet<Item>> resolved = cost instanceof Fixed fixed ? Optional.of(fixed.item().resolve()) : gearMaterial;
        return resolved.filter(items -> items.size() > 0);
    }
}
