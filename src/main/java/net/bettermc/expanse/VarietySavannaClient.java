package net.bettermc.expanse;

import net.bettermc.expanse.entity.ModEntities;
import net.bettermc.expanse.entity.client.OstrichRenderer;
import net.bettermc.expanse.entity.client.SpearRenderer;
import net.bettermc.expanse.entity.client.RhinoRenderer;

import net.bettermc.expanse.fluid.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class VarietySavannaClient implements ClientModInitializer {
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SPEARENTITYTYPE, SpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.RHINO, RhinoRenderer::new);
        EntityRendererRegistry.register(ModEntities.OSTRICH, OstrichRenderer::new);


        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_MURKY_WATER, ModFluids.FLOWING_MURKY_WATER,
                new SimpleFluidRenderHandler(
                        new Identifier("minecraft:block/water_still"),
                        new Identifier("minecraft:block/water_flow"),
                        0xb29062
                ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getCutout(),
                ModFluids.STILL_MURKY_WATER, ModFluids.FLOWING_MURKY_WATER);
    }
}
