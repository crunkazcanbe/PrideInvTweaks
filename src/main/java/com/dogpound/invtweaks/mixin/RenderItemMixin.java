package com.dogpound.invtweaks.mixin;

import com.dogpound.invtweaks.DPInvTweaks;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides items/blocks drawn with the "missing model" OR the "missingno" missing-texture
 * sprite — vanilla inventory, hotbar, dropped items, AND the HEI/JEI lists. renderItem
 * is the single choke-point all item drawing flows through, so cancelling here hides the
 * purple/black checkerboard everywhere at once. Catches both true missing-models and the
 * Orrery-style custom-rendered items whose texture resolved to missingno.
 *
 * (Item centering lives in {@link HbmItemCenterMixin}, which fixes HBM's own GUI offset.)
 */
@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Shadow private ItemModelMesher itemModelMesher;

    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void dpinvtweaks$hideMissing(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if (!DPInvTweaks.hideMissing || itemModelMesher == null || model == null) return;
        try {
            // 1) classic case: the item resolved to the vanilla "missing model".
            if (model == itemModelMesher.getModelManager().getMissingModel()) {
                ci.cancel();
                return;
            }
            // 2) Built-in / TEISR-rendered items (HBM / NTM-Space / Cursed block-items, and many
            //    other mods' custom item renderers) render through their own
            //    TileEntityItemStackRenderer. Those models legitimately report the "missingno"
            //    PARTICLE sprite even though the item draws perfectly — so NEVER hide a built-in
            //    renderer by its particle sprite, or half of HBM disappears. (True missing MODELS
            //    are already caught above in case 1.)
            if (model.isBuiltInRenderer()) return;
            // 3) Orrery-style case: a plain (item/generated) model whose texture resolved to the
            //    "missingno" sprite -> a genuine missing texture (pink/black checkerboard). Hide it.
            net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = model.getParticleTexture();
            if (sprite != null) {
                String name = sprite.getIconName();
                if (name != null && (name.equals("missingno") || name.endsWith("/missingno") || name.endsWith(":missingno"))) {
                    ci.cancel();
                }
            }
        } catch (Throwable ignored) {
            // never break item rendering over a hide check
        }
    }
}
