package net.bettermc.expanse.entity.client;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.custom.RhinoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RhinoModel extends AnimatedGeoModel<RhinoEntity> {
    @Override
    public Identifier getModelResource(RhinoEntity entity) {
        if(!entity.isBaby()) {
            return new Identifier(VarietySavanna.MOD_ID, "geo/rhinospear.geo.json");
        } else {
            return new Identifier(VarietySavanna.MOD_ID, "geo/rhinocerosbaby.geo.json");
        }
    }

    @Override
    public Identifier getTextureResource(RhinoEntity entity) {
        return new Identifier(VarietySavanna.MOD_ID, "textures/entity/rhino/rhinoceros.png");
    }

    @Override
    public Identifier getAnimationResource(RhinoEntity entity) {
        if(!entity.isBaby()) {
            return new Identifier(VarietySavanna.MOD_ID, "animations/rhino.animation.json");
        }
        else {
            return new Identifier(VarietySavanna.MOD_ID, "animations/rhinobaby.animation.json");
        }
    }
}