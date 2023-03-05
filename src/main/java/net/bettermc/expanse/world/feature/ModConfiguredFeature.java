package net.bettermc.expanse.world.feature;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.blocks.ModBlocks;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

import java.util.List;

public class ModConfiguredFeature {

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DOGWOOD_TREE =
            ConfiguredFeatures.register("dogwood_tree", Feature.TREE, new TreeFeatureConfig.Builder(
                    BlockStateProvider.of(ModBlocks.DOGWOOD_LOG),
                    new StraightTrunkPlacer(5, 6, 3),
                    BlockStateProvider.of(ModBlocks.DOGWOOD_LEAVES),
                    new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 4),
                    new TwoLayersFeatureSize(1, 0, 2)).build());

    public static final RegistryEntry<PlacedFeature> DOGWOOD_CHECKED = PlacedFeatures.register("dogwood_checked",
            ModConfiguredFeature.DOGWOOD_TREE, List.of(PlacedFeatures.wouldSurvive(ModBlocks.DOGWOOD_SAPLING)));

    public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> DOGWOOD_SPAWN =
            ConfiguredFeatures.register("dogwood_spawn", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfig(List.of(new RandomFeatureEntry(DOGWOOD_CHECKED, 0.5f)),
                            DOGWOOD_CHECKED));



    public static void registerConfiguredFeatures() {
        VarietySavanna.LOGGER.debug("Registering the ModConfiguredFeatures for " + VarietySavanna.MOD_ID);
    }
}