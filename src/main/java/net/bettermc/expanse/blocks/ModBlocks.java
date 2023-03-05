package net.bettermc.expanse.blocks;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.items.ModItemGroups;
import net.bettermc.expanse.world.feature.tree.DogwoodSaplingGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks
{

    public static final Block STRATA = registerBlock("strata",
            new Block(FabricBlockSettings.copy(Blocks.COBBLESTONE)), ModItemGroups.placeholder);
    public static final Block DENSE_DIRT_BRICK = registerBlock("dense_dirt_brick",
            new Block(FabricBlockSettings.copy(Blocks.BRICK_WALL)), ModItemGroups.placeholder);
    public static final Block DENSE_DIRT_SMOOTH = registerBlock("dense_dirt_smooth",
            new Block(FabricBlockSettings.copy(Blocks.SMOOTH_SANDSTONE)), ModItemGroups.placeholder);
    public static final Block DENSE_DIRT_CARVED = registerBlock("dense_dirt_carved",
            new Block(FabricBlockSettings.copy(Blocks.DIRT)), ModItemGroups.placeholder);
    public static final Block DENSE_DIRT = registerBlock("dense_dirt",
            new Block(FabricBlockSettings.copy(Blocks.DIRT)), ModItemGroups.placeholder);
    public static final Block DOGWOOD_LOG = registerBlock("dogwood_log",
            new PillarBlock(FabricBlockSettings.copy(Blocks.OAK_LOG)), ModItemGroups.placeholder);

    public static final Block DOGWOOD_WOOD = registerBlock("dogwood_wood",
            new PillarBlock(FabricBlockSettings.copy(Blocks.OAK_WOOD)), ModItemGroups.placeholder);
    public static final Block STRIPPED_DOGWOOD_LOG = registerBlock("stripped_dogwood_log",
            new PillarBlock(FabricBlockSettings.copy(Blocks.STRIPPED_OAK_LOG)), ModItemGroups.placeholder);
    public static final Block STRIPPED_DOGWOOD_WOOD = registerBlock("stripped_dogwood_wood",
            new PillarBlock(FabricBlockSettings.copy(Blocks.STRIPPED_OAK_WOOD)), ModItemGroups.placeholder);

    public static final Block DOGWOOD_PLANKS = registerBlock("dogwood_planks",
            new Block(FabricBlockSettings.copy(Blocks.OAK_PLANKS)), ModItemGroups.placeholder);
    public static final Block DOGWOOD_LEAVES = registerBlock("dogwood_leaves",
            new LeavesBlock(FabricBlockSettings.copy(Blocks.OAK_LEAVES)), ModItemGroups.placeholder);
    public static final Block OSTRICH_EGG = registerBlock("ostrich_egg",
            new OstrichEggBlock(FabricBlockSettings.copy(Blocks.TURTLE_EGG)), ModItemGroups.placeholder);
    public static final Block DOGWOOD_SAPLING = registerBlock("dogwood_sapling",
            new SaplingBlock(new DogwoodSaplingGenerator(),
                    FabricBlockSettings.copy(Blocks.OAK_SAPLING)), ModItemGroups.placeholder);


    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(VarietySavanna.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(VarietySavanna.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, new Identifier(VarietySavanna.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }

    public static void registerModBlocks() {
        VarietySavanna.LOGGER.debug("Registering ModBlocks for " + VarietySavanna.MOD_ID);
    }
}
