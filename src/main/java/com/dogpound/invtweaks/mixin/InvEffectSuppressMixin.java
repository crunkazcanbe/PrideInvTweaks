package com.dogpound.invtweaks.mixin;

import com.dogpound.invtweaks.DPInvTweaks;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When effectsLeft is on, cancel vanilla's own next-to-the-inventory potion draw so it isn't drawn
 * twice — GuiContainerEffectMixin now draws it pinned to the far left instead.
 */
@Mixin(InventoryEffectRenderer.class)
public class InvEffectSuppressMixin {

    @Inject(method = "drawActivePotionEffects", at = @At("HEAD"), cancellable = true)
    private void dpinvtweaks$suppressVanillaEffects(CallbackInfo ci) {
        if (DPInvTweaks.effectsLeft) ci.cancel();
    }
}
