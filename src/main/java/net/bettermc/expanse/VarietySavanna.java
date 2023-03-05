package net.bettermc.expanse;

import net.bettermc.expanse.fluid.ModFluids;
import net.bettermc.expanse.items.ModItems;
import net.bettermc.expanse.util.ModRegistries;
import net.bettermc.expanse.world.gen.WorldGen;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.TerraBlenderApi;

public class VarietySavanna implements ModInitializer, TerraBlenderApi {

    public static final String MOD_ID = "expanse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onTerraBlenderInitialized()
    {
    }
    @Override
    public void onInitialize() {

        ModItems.registerModItems();

        ModRegistries.registerModStuffs();
        ModFluids.register();
        WorldGen.generateModWorldGen();
    }

}
