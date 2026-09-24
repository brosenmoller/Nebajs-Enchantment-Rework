package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.BookshelfScanner;
import com.nebaj.nebajsenchantmentrework.tags.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin
{
    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Unique
    private Set<Holder<Enchantment>> nebajsenchantmentrework$unlocked = Set.of();

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void nebajsenchantmentrework$onSlotsChanged(Container container, CallbackInfo callbackInfo)
    {
        nebajsenchantmentrework$rescan();
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void nebajsenchantmentrework$onClickMenuButton(Player player, int buttonId, CallbackInfoReturnable<Boolean> callbackInfoReturnable)
    {
        nebajsenchantmentrework$rescan();
    }

    @ModifyArg(method = "getEnchantmentList", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"),
            index = 3)
    private Stream<Holder<Enchantment>> nebajsenchantmentrework$filterPool(Stream<Holder<Enchantment>> vanilla)
    {
        return Stream.concat(
                vanilla.filter(enchantmentHolder -> !enchantmentHolder.is(ModTags.Enchantments.ENCHANTINGTABLE_ENCHANTMENTS_REQUIRING_BOOK)),
                nebajsenchantmentrework$unlocked.stream()
        );
    }

    @Unique
    private void nebajsenchantmentrework$rescan()
    {
        access.execute((level, blockPos) -> nebajsenchantmentrework$unlocked = BookshelfScanner.scan(level, blockPos));
    }
}