package com.dogpound.invtweaks.mixin;

import com.dogpound.invtweaks.DPInvTweaks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * Draws the player's active potion effects pinned to the far LEFT of EVERY container GUI — vanilla
 * inventory, creative, and every mod's GUI (they all extend GuiContainer). Vanilla only drew them next
 * to the survival/creative inventory; InvEffectSuppressMixin hides that draw so this is the only one.
 * Faithful copy of vanilla's InventoryEffectRenderer.drawActivePotionEffects, drawn 20px left of the
 * vanilla spot (guiLeft - 144).
 */
@Mixin(GuiContainer.class)
public abstract class GuiContainerEffectMixin extends GuiScreen {

    @Shadow protected int guiTop;
    @Shadow protected int guiLeft;

    private static final ResourceLocation DP_INV_BG = new ResourceLocation("textures/gui/container/inventory.png");

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void dpinvtweaks$drawEffectsLeft(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!DPInvTweaks.effectsLeft) return;
        if (this.mc == null || this.mc.player == null) return;
        Collection<PotionEffect> effects = this.mc.player.getActivePotionEffects();
        if (effects.isEmpty()) return;

        // Vanilla draws the box at guiLeft - 124 (tucked left of the inventory). She wanted it nudged
        // 20px further left → guiLeft - 144. Clamp so wide mod GUIs don't push it off the screen edge.
        int x = Math.max(2, this.guiLeft - 144);
        int y = this.guiTop;
        int step = 33;
        if (effects.size() > 5) step = 132 / (effects.size() - 1);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        for (PotionEffect effect : effects) {
            Potion potion = effect.getPotion();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(DP_INV_BG);
            this.drawTexturedModalRect(x, y, 0, 166, 140, 32);
            if (potion.hasStatusIcon()) {
                int idx = potion.getStatusIconIndex();
                this.drawTexturedModalRect(x + 6, y + 7, idx % 8 * 18, 198 + idx / 8 * 18, 18, 18);
            }
            String name = I18n.format(potion.getName());
            int amp = effect.getAmplifier();
            if (amp >= 1 && amp <= 9) {
                name = name + " " + I18n.format("potion.potency." + amp);
            }
            this.fontRenderer.drawStringWithShadow(name, x + 10 + 18, y + 6, 0xFFFFFF);
            String dur = Potion.getPotionDurationString(effect, 1.0F);
            this.fontRenderer.drawStringWithShadow(dur, x + 10 + 18, y + 6 + 10, 0x7F7F7F);
            y += step;
        }
    }
}
