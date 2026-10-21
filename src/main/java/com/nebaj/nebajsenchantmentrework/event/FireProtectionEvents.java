package com.nebaj.nebajsenchantmentrework.event;

import com.nebaj.nebajsenchantmentrework.config.ModConfigManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public final class FireProtectionEvents
{
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    private FireProtectionEvents() {}

    public static void initialize()
    {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(FireProtectionEvents::allowDamage);
    }

    private static boolean allowDamage(LivingEntity entity, DamageSource source, float amount)
    {
        if (!ModConfigManager.current().enchantmentBehaviour.maxedFireProtectionImmunity) return true;
        if (!source.is(DamageTypeTags.IS_FIRE)) return true;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        return !hasFullMaxedFireProtection(entity);
    }

    private static boolean hasFullMaxedFireProtection(LivingEntity entity)
    {
        Holder<Enchantment> fireProtection = entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_PROTECTION);
        int maxLevel = fireProtection.value().getMaxLevel();

        for (EquipmentSlot slot : ARMOR_SLOTS)
        {
            if (EnchantmentHelper.getItemEnchantmentLevel(fireProtection, entity.getItemBySlot(slot)) < maxLevel) return false;
        }
        return true;
    }
}
