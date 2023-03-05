package net.bettermc.expanse.entity.client;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.custom.OstrichEntity;
import net.bettermc.expanse.entity.custom.RhinoEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OstrichRenderer extends GeoEntityRenderer<OstrichEntity> {
    public OstrichRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new OstrichModel());
        this.shadowRadius = 0.6f;
    }

    @Override
    public Identifier getTextureResource(OstrichEntity entity) {
        return new Identifier(VarietySavanna.MOD_ID, "textures/entity/rhino/rhinoceros.png");
    }

    @Override
    public RenderLayer getRenderType(OstrichEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {

        stack.scale(1.2f, 1.2f, 1.2f);

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}