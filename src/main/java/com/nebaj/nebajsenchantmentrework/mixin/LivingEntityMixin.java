package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nebaj.nebajsenchantmentrework.attribute.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    // Mirrors how travelInWater applies WATER_MOVEMENT_EFFICIENCY: speed and horizontal drag are interpolated towards their on-land values
    @Unique
    private static final float LAND_DRAG = 0.54600006f;
    @Unique
    private static final float LAVA_SPEED = 0.02f;
    @Unique
    private static final double LAVA_DRAG = 0.5;

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder nebajsenchantmentrework$addLavaMovementEfficiency(AttributeSupplier.Builder builder)
    {
        return builder.add(ModAttributes.LAVA_MOVEMENT_EFFICIENCY);
    }

    @ModifyArg(method = "travelInLava", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float nebajsenchantmentrework$applyLavaSpeed(float speed)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        return speed + (self.getSpeed() - LAVA_SPEED) * nebajsenchantmentrework$getLavaMovementEfficiency();
    }

    @WrapOperation(method = "travelInLava", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 nebajsenchantmentrework$applyShallowLavaDrag(Vec3 movement, double x, double y, double z, Operation<Vec3> original)
    {
        double drag = nebajsenchantmentrework$getLavaDrag();
        return original.call(movement, drag, y, drag);
    }

    @WrapOperation(method = "travelInLava", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 nebajsenchantmentrework$applyDeepLavaDrag(Vec3 movement, double scale, Operation<Vec3> original)
    {
        double efficiency = nebajsenchantmentrework$getLavaMovementEfficiency();
        if (efficiency <= 0) return original.call(movement, scale);

        double drag = nebajsenchantmentrework$getLavaDrag();
        return movement.multiply(drag, scale, drag);
    }

    @Unique
    private double nebajsenchantmentrework$getLavaDrag()
    {
        return LAVA_DRAG + (LAND_DRAG - LAVA_DRAG) * nebajsenchantmentrework$getLavaMovementEfficiency();
    }

    @Unique
    private float nebajsenchantmentrework$getLavaMovementEfficiency()
    {
        LivingEntity self = (LivingEntity) (Object) this;
        float efficiency = (float) self.getAttributeValue(ModAttributes.LAVA_MOVEMENT_EFFICIENCY);
        return self.onGround() ? efficiency : efficiency * 0.75f;
    }
}
