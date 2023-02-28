package net.bettermc.expanse.util;

import net.bettermc.expanse.blocks.ModBlocks;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

public class ModStrippableBlocks {
    public static void registerStrippables() {
        StrippableBlockRegistry.register(ModBlocks.DOGWOOD_LOG, ModBlocks.STRIPPED_DOGWOOD_LOG);
        StrippableBlockRegistry.register(ModBlocks.DOGWOOD_WOOD, ModBlocks.STRIPPED_DOGWOOD_WOOD);
    }
}