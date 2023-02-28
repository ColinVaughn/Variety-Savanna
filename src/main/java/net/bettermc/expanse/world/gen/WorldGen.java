package net.bettermc.expanse.world.gen;

public class WorldGen {
    public static void generateModWorldGen() {
        ModEntitySpawn.addEntitySpawn();
        ModTreeGen.generateTrees();
    }
}
