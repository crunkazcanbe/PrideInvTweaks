package com.dogpound.invtweaks.mixin;

import com.dogpound.invtweaks.DPInvTweaks;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Centers HBM / NTM-Space / Cursed item icons in their GUI slots.
 *
 * <p>HBM draws its block-items in the inventory through a custom renderer
 * ({@code com.hbm.render.item.ItemRenderBase#renderByItem}). The GUI branch hardcodes:
 * <pre>rotate(30,1,0,0); rotate(225,0,1,0); scale(0.062); translate(0, 11.3, -11.3);</pre>
 * That {@code translate(0, 11.3, -11.3)} is what pushes the icon up onto the slot's grid
 * line. We redirect just that one translate call and substitute a tunable Y (and keep Z),
 * so the icon can be nudged back to dead center without touching HBM's jar.
 *
 * <p>{@code DPInvTweaks.centerYOffset} is read live; edit it in config/dpinvtweaks.cfg
 * (11.3 = HBM original/high; lower = icon moves down). Gated by the centerItems toggle.
 * remap=false on the class (HBM is a mod class, not obfuscated); the GlStateManager call
 * inside is vanilla so that @At is remapped normally.
 */
@Mixin(targets = "com.hbm.render.item.ItemRenderBase", remap = false)
public class HbmItemCenterMixin {

    private static boolean dpinvtweaks$logged = false;

    @Redirect(
        method = {"func_179022_a", "renderByItem"},  // SRG name (production) + deobf (dev)
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V",
                 remap = true),
        require = 0,   // never crash the game if HBM's method shape differs — just no-op
        expect = 0
    )
    private void dpinvtweaks$centerGuiTranslate(float x, float y, float z) {
        // Identify HBM's GUI-case translate by its exact constants and retarget Y.
        if (DPInvTweaks.centerItems && x == 0.0F && y == 11.3F && z == -11.3F) {
            if (!dpinvtweaks$logged) {
                dpinvtweaks$logged = true;
                System.out.println("[DPInvTweaks] HBM item-centering active; Y=" + DPInvTweaks.centerYOffset
                        + " scale=" + DPInvTweaks.centerScale);
            }
            GlStateManager.translate(0.0F, DPInvTweaks.centerYOffset, -11.3F);
        } else {
            GlStateManager.translate(x, y, z);
        }
    }

    /**
     * HBM's GUI item scale is a hardcoded 0.062 — too large here, so block-items overflow the
     * slot. Redirect ONLY that exact scale call (the other scale(...) calls in renderByItem are
     * for ground/fixed/hand transforms and pass through untouched) and shrink to centerScale.
     */
    @Redirect(
        method = {"func_179022_a", "renderByItem"},
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V",
                 remap = true),
        require = 0,
        expect = 0
    )
    private void dpinvtweaks$centerGuiScale(float x, float y, float z) {
        if (DPInvTweaks.centerItems && x == 0.062F && y == 0.062F && z == 0.062F) {
            float s = DPInvTweaks.centerScale;
            GlStateManager.scale(s, s, s);
        } else {
            GlStateManager.scale(x, y, z);
        }
    }
}
