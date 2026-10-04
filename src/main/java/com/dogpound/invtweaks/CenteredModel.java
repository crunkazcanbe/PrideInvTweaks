package com.dogpound.invtweaks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * A pass-through {@link IBakedModel} wrapper that re-centers an item in its GUI slot.
 *
 * <p>It delegates every call to the real model EXCEPT {@link #handlePerspective}: for the
 * {@code GUI} transform it strips out the translation component of the model's transform
 * matrix (m03/m13/m23) while keeping rotation + scale. That cancels whatever per-model
 * offset was pushing the item off-center (the "sitting on top of the slot lines" look)
 * without flattening 3D block-items — their isometric rotation/scale is preserved.
 *
 * <p>For flat {@code item/generated} items the GUI transform has no translation anyway, so
 * wrapping them is a harmless no-op. Used by RenderItemMixin only when centering is enabled.
 */
public final class CenteredModel implements IBakedModel {

    private final IBakedModel delegate;

    public CenteredModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
        Pair<? extends IBakedModel, Matrix4f> pair = delegate.handlePerspective(type);
        if (type != TransformType.GUI) return pair;

        Matrix4f m = pair.getRight() == null ? new Matrix4f() : new Matrix4f(pair.getRight());
        if (pair.getRight() == null) m.setIdentity();
        // Zero only the translation column -> item is forced back to the slot center.
        m.m03 = 0.0f;
        m.m13 = 0.0f;
        m.m23 = 0.0f;
        return Pair.of(pair.getLeft(), m);
    }

    // ---- everything else delegates straight through ----

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return delegate.getQuads(state, side, rand);
    }

    @Override public boolean isAmbientOcclusion() { return delegate.isAmbientOcclusion(); }
    @Override public boolean isAmbientOcclusion(IBlockState state) { return delegate.isAmbientOcclusion(state); }
    @Override public boolean isGui3d() { return delegate.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return delegate.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return delegate.getParticleTexture(); }

    @SuppressWarnings("deprecation")
    @Override public ItemCameraTransforms getItemCameraTransforms() { return delegate.getItemCameraTransforms(); }

    @Override public ItemOverrideList getOverrides() { return delegate.getOverrides(); }
}
