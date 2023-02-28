package net.bettermc.expanse.util;

import net.bettermc.expanse.entity.ModEntities;
import net.bettermc.expanse.entity.custom.RhinoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class ModRegistries {
    public static void registerModStuffs() {

        registerAttributes();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.RHINO, RhinoEntity.setAttributes());
    }

    }
