package net.bettermc.expanse;

import net.bettermc.expanse.entity.custom.PackedSnowballEntity;
import net.bettermc.expanse.items.ModItems;
import net.bettermc.expanse.util.ModRegistries;
import net.bettermc.expanse.world.gen.WorldGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VarietySavanna implements ModInitializer {

    public static final String MOD_ID = "expanse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerModItems();

        ModRegistries.registerModStuffs();
        WorldGen.generateModWorldGen();
    }

}
