package net.bettermc.expanse;

import net.bettermc.expanse.entity.ModEntities;
import net.bettermc.expanse.entity.client.RhinoRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class VarietySavannaClient implements ClientModInitializer {
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.PackedSnowballEntityType, (context) ->
                new FlyingItemEntityRenderer(context));
        EntityRendererRegistry.register(ModEntities.RHINO, RhinoRenderer::new);

    }
}
