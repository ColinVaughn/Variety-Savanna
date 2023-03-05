package net.bettermc.expanse.entity.client;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.custom.SpearEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;
@Environment(EnvType.CLIENT)
public class SpearModel extends AnimatedGeoModel<SpearEntity> {


    // METHODS //
    // PUBLIC
    @Override
    public Identifier getAnimationResource(SpearEntity animatable) {
        return null;
    }

    @Override
    public Identifier getModelResource(SpearEntity object) {
        return new Identifier(VarietySavanna.MOD_ID, "geo/rhinospear.geo.json");
    }

    @Override
    public Identifier getTextureResource(SpearEntity object) {
        return new Identifier(VarietySavanna.MOD_ID, "textures/entity/rhinospear/rhinospear.png");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setLivingAnimations(SpearEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}