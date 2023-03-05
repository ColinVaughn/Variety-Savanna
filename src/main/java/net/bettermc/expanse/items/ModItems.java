package net.bettermc.expanse.items;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item PLACE_HOLDER_INGOT = registerItem(
        "place_holder_ingot",
        new Item(new FabricItemSettings().group(ModItemGroups.placeholder))
    );
    public static final Item RHINO_LEATHER = registerItem(
            "rhino_leather",
            new Item(new FabricItemSettings().group(ModItemGroups.placeholder))
    );
    public static final Item RHINO_WEAPON = registerItem("rhino_weapon",
            new SpearWeaponItem(new Item.Settings().maxCount(1).maxDamage(465).group(ModItemGroups.placeholder))
    );

    public static final Item RHINO_HORN = registerItem("rhino_horn",
            new Item(new FabricItemSettings().group(ModItemGroups.placeholder))
    );




    //EGGS
    public static final Item RHINO_SPAWN_EGG = registerItem("rhino_spawn_egg",
            new SpawnEggItem(ModEntities.RHINO,0x948e8d, 0x3b3635,
                    new FabricItemSettings().group(ModItemGroups.placeholder)));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(VarietySavanna.MOD_ID, name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering Mod Items for " + VarietySavanna.MOD_ID);
    }
}
