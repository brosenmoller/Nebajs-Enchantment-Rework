package com.nebaj.nebajsenchantmentrework.client;

import com.nebaj.nebajsenchantmentrework.NebajSEnchantmentRework;
import com.nebaj.nebajsenchantmentrework.attachment.ModAttachments;
import com.nebaj.nebajsenchantmentrework.attachment.MomentumState;
import com.nebaj.nebajsenchantmentrework.attachment.SurgingState;
import com.nebaj.nebajsenchantmentrework.component.ModDataComponents;
import com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments;
import com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments.SurgingStatus;
import com.nebaj.nebajsenchantmentrework.enchantment.ModEnchantments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.UUID;

import static com.nebaj.nebajsenchantmentrework.enchantment.MiningSpeedEnchantments.mainHandLevel;

public class MiningEnchantmentBar implements ContextualBar
{
    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/experience_bar_background");
    private static final Identifier BLUE_SPRITE = NebajSEnchantmentRework.id("hud/mining_bar_blue");
    private static final Identifier ORANGE_SPRITE = NebajSEnchantmentRework.id("hud/mining_bar_orange");

    public static final MiningEnchantmentBar INSTANCE = new MiningEnchantmentBar();

    private record Fill(float fraction, Identifier sprite) {}

    private MiningEnchantmentBar() {}

    public static boolean shouldReplaceContextualBar(Minecraft minecraft)
    {
        if (minecraft.player == null || minecraft.gameMode == null) return false;
        if (!minecraft.gameMode.hasExperience()) return false;

        return currentFill(minecraft.player) != null;
    }

    private static Fill currentFill(LocalPlayer player)
    {
        if (mainHandLevel(player, ModEnchantments.SURGING) > 0)
        {
            UUID pickaxeId = player.getMainHandItem().get(ModDataComponents.SURGING_ID);
            Map<UUID, SurgingState> states = player.getAttachedOrElse(ModAttachments.SURGING, Map.of());
            SurgingState state = pickaxeId == null ? null : states.get(pickaxeId);
            SurgingStatus status = MiningSpeedEnchantments.surgingStatus(state, player.level().getGameTime());
            return new Fill(status.charge(), status.coolingDown() ? ORANGE_SPRITE : BLUE_SPRITE);
        }

        int momentumLevel = mainHandLevel(player, ModEnchantments.MOMENTUM);
        if (momentumLevel > 0)
        {
            MomentumState state = player.getAttached(ModAttachments.MOMENTUM);
            float buildUp = state == null ? 0.0F : MiningSpeedEnchantments.momentumBuildUp(state.count(), momentumLevel);
            return new Fill(buildUp, BLUE_SPRITE);
        }

        return null;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        int left = left(minecraft.getWindow());
        int top = top(minecraft.getWindow());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, left, top, WIDTH, HEIGHT);

        Fill fill = currentFill(minecraft.player);
        if (fill == null) return;

        int filledWidth = Mth.lerpDiscrete(fill.fraction(), 0, WIDTH);
        if (filledWidth <= 0) return;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, fill.sprite(), WIDTH, HEIGHT, 0, 0, left, top, filledWidth, HEIGHT);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {
    }
}
