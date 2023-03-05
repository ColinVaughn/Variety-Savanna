package net.bettermc.expanse.entity.client;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.custom.SpearEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class SpearRenderer extends GeoProjectilesRenderer<SpearEntity> {

    // CONSTRUCTOR //
    public SpearRenderer(Context ctx) {
        super(ctx, new SpearModel());
    }
    @Override
    public Identifier getTexture(SpearEntity instance) {
        return new Identifier(VarietySavanna.MOD_ID, "textures/entity/rhinospear/rhinospear.png");
    }


    public RenderLayer getRenderType(SpearEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
    }
}