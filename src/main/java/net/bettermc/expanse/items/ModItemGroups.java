package net.bettermc.expanse.items;

import net.bettermc.expanse.VarietySavanna;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup placeholder = FabricItemGroupBuilder.build(
        new Identifier(VarietySavanna.MOD_ID, "placeholder"),
        () -> new ItemStack(ModItems.RHINO_SPAWN_EGG)
    );
}
