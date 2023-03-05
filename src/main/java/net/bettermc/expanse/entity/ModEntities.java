package net.bettermc.expanse.entity;

import net.bettermc.expanse.VarietySavanna;
import net.bettermc.expanse.entity.custom.OstrichEntity;
import net.bettermc.expanse.entity.custom.SpearEntity;
import net.bettermc.expanse.entity.custom.RhinoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities  {

    public static final EntityType<RhinoEntity> RHINO = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(VarietySavanna.MOD_ID, "rhino"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RhinoEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f, 1.4f)).build());
    public static final EntityType<OstrichEntity> OSTRICH = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(VarietySavanna.MOD_ID, "ostrich"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OstrichEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f, 1.4f)).build());
    public static final EntityType<SpearEntity> SPEARENTITYTYPE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(VarietySavanna.MOD_ID, "spear"),
            FabricEntityTypeBuilder.<SpearEntity>create(SpawnGroup.MISC, SpearEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());


}


