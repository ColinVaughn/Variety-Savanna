package net.bettermc.expanse.fluid;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.items.ModItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModFluids {
    public static FlowableFluid STILL_MURKY_WATER;
    public static FlowableFluid FLOWING_MURKY_WATER;
    public static Block MURKY_WATER_BLOCK;
    public static Item MURKY_WATER_BUCKET;

    public static void register() {
        STILL_MURKY_WATER = Registry.register(Registry.FLUID,
                new Identifier(VarietySavanna.MOD_ID, "murky_water"), new MurkyWaterFluid.Still());
        FLOWING_MURKY_WATER = Registry.register(Registry.FLUID,
                new Identifier(VarietySavanna.MOD_ID, "flowing_murky_water"), new MurkyWaterFluid.Flowing());

        MURKY_WATER_BLOCK = Registry.register(Registry.BLOCK, new Identifier(VarietySavanna.MOD_ID, "murky_water_block"),
                new FluidBlock(ModFluids.STILL_MURKY_WATER, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        MURKY_WATER_BUCKET = Registry.register(Registry.ITEM, new Identifier(VarietySavanna.MOD_ID, "murky_water_bucket"),
                new BucketItem(ModFluids.STILL_MURKY_WATER, new FabricItemSettings().group(ModItemGroups.placeholder).recipeRemainder(Items.BUCKET).maxCount(1)));
    }
}