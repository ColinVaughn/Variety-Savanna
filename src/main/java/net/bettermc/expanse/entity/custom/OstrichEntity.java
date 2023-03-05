package net.bettermc.expanse.entity.custom;

import net.bettermc.expanse.blocks.ModBlocks;
import net.bettermc.expanse.blocks.OstrichEggBlock;
import net.bettermc.expanse.entity.ModEntities;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlSwimNavigation;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.UUID;
import java.util.function.Predicate;

public class OstrichEntity extends AnimalEntity implements Angerable, IAnimatable {
    private static final TrackedData<BlockPos> HOME_POS;
    private static final TrackedData<Boolean> HAS_EGG;
    private static final TrackedData<Boolean> DIGGING_SAND;
    private static final TrackedData<BlockPos> TRAVEL_POS;
    private static final TrackedData<Boolean> LAND_BOUND;
    private static final TrackedData<Boolean> ACTIVELY_TRAVELING;
    public static final Ingredient BREEDING_ITEM;
    private AnimationFactory factory = new AnimationFactory(this);
    int sandDiggingCounter;
    public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER;

    public OstrichEntity(EntityType<? extends OstrichEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1.0F);
        this.moveControl = new OstrichEntity.TurtleMoveControl(this);
        this.stepHeight = 1.0F;
    }

    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }

    BlockPos getHomePos() {
        return (BlockPos)this.dataTracker.get(HOME_POS);
    }

    void setTravelPos(BlockPos pos) {
        this.dataTracker.set(TRAVEL_POS, pos);
    }

    BlockPos getTravelPos() {
        return (BlockPos)this.dataTracker.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return (Boolean)this.dataTracker.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    public boolean isDiggingSand() {
        return (Boolean)this.dataTracker.get(DIGGING_SAND);
    }

    void setDiggingSand(boolean diggingSand) {
        this.sandDiggingCounter = diggingSand ? 1 : 0;
        this.dataTracker.set(DIGGING_SAND, diggingSand);
    }

    boolean isLandBound() {
        return (Boolean)this.dataTracker.get(LAND_BOUND);
    }

    void setLandBound(boolean landBound) {
        this.dataTracker.set(LAND_BOUND, landBound);
    }

    boolean isActivelyTraveling() {
        return (Boolean)this.dataTracker.get(ACTIVELY_TRAVELING);
    }

    void setActivelyTraveling(boolean traveling) {
        this.dataTracker.set(ACTIVELY_TRAVELING, traveling);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(HAS_EGG, false);
        this.dataTracker.startTracking(TRAVEL_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(LAND_BOUND, false);
        this.dataTracker.startTracking(ACTIVELY_TRAVELING, false);
        this.dataTracker.startTracking(DIGGING_SAND, false);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("HomePosX", this.getHomePos().getX());
        nbt.putInt("HomePosY", this.getHomePos().getY());
        nbt.putInt("HomePosZ", this.getHomePos().getZ());
        nbt.putBoolean("HasEgg", this.hasEgg());
        nbt.putInt("TravelPosX", this.getTravelPos().getX());
        nbt.putInt("TravelPosY", this.getTravelPos().getY());
        nbt.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        int i = nbt.getInt("HomePosX");
        int j = nbt.getInt("HomePosY");
        int k = nbt.getInt("HomePosZ");
        this.setHomePos(new BlockPos(i, j, k));
        super.readCustomDataFromNbt(nbt);
        this.setHasEgg(nbt.getBoolean("HasEgg"));
        int l = nbt.getInt("TravelPosX");
        int m = nbt.getInt("TravelPosY");
        int n = nbt.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(l, m, n));
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setHomePos(this.getBlockPos());
        this.setTravelPos(BlockPos.ORIGIN);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public static boolean canSpawn(EntityType<OstrichEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return pos.getY() < world.getSeaLevel() + 4 && OstrichEggBlock.isSandBelow(world, pos) && isLightLevelValidForNaturalSpawn(world, pos);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new OstrichEntity.TurtleEscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new OstrichEntity.MateGoal(this, 1.0));
        this.goalSelector.add(1, new OstrichEntity.LayEggGoal(this, 1.0));
        this.goalSelector.add(2, new TemptGoal(this, 1.1, BREEDING_ITEM, false));
        this.goalSelector.add(3, new OstrichEntity.WanderInWaterGoal(this, 1.0));
        this.goalSelector.add(4, new OstrichEntity.GoHomeGoal(this, 1.0));
        this.goalSelector.add(7, new OstrichEntity.TravelGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new OstrichEntity.WanderOnLandGoal(this, 1.0, 100));
    }

    public static DefaultAttributeContainer.Builder createTurtleAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    public boolean isPushedByFluids() {
        return false;
    }

    public boolean canBreatheInWater() {
        return true;
    }

    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    public int getMinAmbientSoundDelay() {
        return 200;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return !this.isTouchingWater() && this.onGround && !this.isBaby() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
    }

    protected void playSwimSound(float volume) {
        super.playSwimSound(volume * 1.5F);
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_TURTLE_SWIM;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isBaby() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return this.isBaby() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent soundEvent = this.isBaby() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
        this.playSound(soundEvent, 0.15F, 1.0F);
    }

    public boolean canEat() {
        return super.canEat() && !this.hasEgg();
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.15F;
    }

    public float getScaleFactor() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    protected EntityNavigation createNavigation(World world) {
        return new OstrichEntity.TurtleSwimNavigation(this, world);
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return (PassiveEntity) ModEntities.OSTRICH.create(world);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Blocks.SEAGRASS.asItem());
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (!this.isLandBound() && world.getFluidState(pos).isIn(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return OstrichEggBlock.isSandBelow(world, pos) ? 10.0F : world.getPhototaxisFavor(pos);
        }
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0) {
            BlockPos blockPos = this.getBlockPos();
            if (OstrichEggBlock.isSandBelow(this.world, blockPos)) {
                this.world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(this.world.getBlockState(blockPos.down())));
            }
        }

    }

    protected void onGrowUp() {
        super.onGrowUp();
        if (!this.isBaby() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SCUTE, 1);
        }

    }

    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null && (!this.isLandBound() || !this.getHomePos().isWithinDistance(this.getPos(), 20.0))) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }

    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static {
        HOME_POS = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        HAS_EGG = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DIGGING_SAND = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        TRAVEL_POS = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        LAND_BOUND = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ACTIVELY_TRAVELING = DataTracker.registerData(OstrichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BREEDING_ITEM = Ingredient.ofItems(new ItemConvertible[]{Blocks.SEAGRASS.asItem()});
        BABY_TURTLE_ON_LAND_FILTER = (entity) -> {
            return entity.isBaby() && !entity.isTouchingWater();
        };
    }

    @Override
    public int getAngerTime() {
        return 0;
    }

    @Override
    public void setAngerTime(int angerTime) {

    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return null;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {

    }

    @Override
    public void chooseRandomAngerTime() {

    }



    @Nullable
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "animations", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.dead) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.death", true));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.idle", true));
        return PlayState.CONTINUE;
    }


    static class TurtleMoveControl extends MoveControl {
        private final OstrichEntity ostrich;

        TurtleMoveControl(OstrichEntity ostrich) {
            super(ostrich);
            this.ostrich = ostrich;
        }

        private void updateVelocity() {
            if (this.ostrich.isTouchingWater()) {
                this.ostrich.setVelocity(this.ostrich.getVelocity().add(0.0, 0.005, 0.0));
                if (!this.ostrich.getHomePos().isWithinDistance(this.ostrich.getPos(), 16.0)) {
                    this.ostrich.setMovementSpeed(Math.max(this.ostrich.getMovementSpeed() / 2.0F, 0.08F));
                }

                if (this.ostrich.isBaby()) {
                    this.ostrich.setMovementSpeed(Math.max(this.ostrich.getMovementSpeed() / 3.0F, 0.06F));
                }
            } else if (this.ostrich.onGround) {
                this.ostrich.setMovementSpeed(Math.max(this.ostrich.getMovementSpeed() / 2.0F, 0.06F));
            }

        }

        public void tick() {
            this.updateVelocity();
            if (this.state == State.MOVE_TO && !this.ostrich.getNavigation().isIdle()) {
                double d = this.targetX - this.ostrich.getX();
                double e = this.targetY - this.ostrich.getY();
                double f = this.targetZ - this.ostrich.getZ();
                double g = Math.sqrt(d * d + e * e + f * f);
                e /= g;
                float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F;
                this.ostrich.setYaw(this.wrapDegrees(this.ostrich.getYaw(), h, 90.0F));
                this.ostrich.bodyYaw = this.ostrich.getYaw();
                float i = (float)(this.speed * this.ostrich.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                this.ostrich.setMovementSpeed(MathHelper.lerp(0.125F, this.ostrich.getMovementSpeed(), i));
                this.ostrich.setVelocity(this.ostrich.getVelocity().add(0.0, (double)this.ostrich.getMovementSpeed() * e * 0.1, 0.0));
            } else {
                this.ostrich.setMovementSpeed(0.0F);
            }
        }
    }

    private static class TurtleEscapeDangerGoal extends EscapeDangerGoal {
        TurtleEscapeDangerGoal(OstrichEntity ostrich, double speed) {
            super(ostrich, speed);
        }

        public boolean canStart() {
            if (!this.isInDanger()) {
                return false;
            } else {
                BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 7);
                if (blockPos != null) {
                    this.targetX = (double)blockPos.getX();
                    this.targetY = (double)blockPos.getY();
                    this.targetZ = (double)blockPos.getZ();
                    return true;
                } else {
                    return this.findTarget();
                }
            }
        }
    }

    private static class MateGoal extends AnimalMateGoal {
        private final OstrichEntity ostrich;

        MateGoal(OstrichEntity ostrich, double speed) {
            super(ostrich, speed);
            this.ostrich = ostrich;
        }

        public boolean canStart() {
            return super.canStart() && !this.ostrich.hasEgg();
        }

        protected void breed() {
            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
            if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
                serverPlayerEntity = this.mate.getLovingPlayer();
            }

            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, (PassiveEntity)null);
            }

            this.ostrich.setHasEgg(true);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            Random random = this.animal.getRandom();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }

    private static class LayEggGoal extends MoveToTargetPosGoal {
        private final OstrichEntity ostrich;

        LayEggGoal(OstrichEntity ostrich, double speed) {
            super(ostrich, speed, 16);
            this.ostrich = ostrich;
        }

        public boolean canStart() {
            return this.ostrich.hasEgg() && this.ostrich.getHomePos().isWithinDistance(this.ostrich.getPos(), 9.0) ? super.canStart() : false;
        }

        public boolean shouldContinue() {
            return super.shouldContinue() && this.ostrich.hasEgg() && this.ostrich.getHomePos().isWithinDistance(this.ostrich.getPos(), 9.0);
        }

        public void tick() {
            super.tick();
            BlockPos blockPos = this.ostrich.getBlockPos();
            if (!this.ostrich.isTouchingWater() && this.hasReached()) {
                if (this.ostrich.sandDiggingCounter < 1) {
                    this.ostrich.setDiggingSand(true);
                } else if (this.ostrich.sandDiggingCounter > this.getTickCount(200)) {
                    World world = this.ostrich.world;
                    world.playSound((PlayerEntity)null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                    world.setBlockState(this.targetPos.up(), (BlockState) ModBlocks.OSTRICH_EGG.getDefaultState().with(OstrichEggBlock.EGGS, this.ostrich.random.nextInt(4) + 1), 3);
                    this.ostrich.setHasEgg(false);
                    this.ostrich.setDiggingSand(false);
                    this.ostrich.setLoveTicks(600);
                }

                if (this.ostrich.isDiggingSand()) {
                    ++this.ostrich.sandDiggingCounter;
                }
            }

        }

        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return !world.isAir(pos.up()) ? false : OstrichEggBlock.isSand(world, pos);
        }
    }

    private static class WanderInWaterGoal extends MoveToTargetPosGoal {
        private static final int field_30385 = 1200;
        private final OstrichEntity ostrich;

        WanderInWaterGoal(OstrichEntity ostrich, double speed) {
            super(ostrich, ostrich.isBaby() ? 2.0 : speed, 24);
            this.ostrich = ostrich;
            this.lowestY = -1;
        }

        public boolean shouldContinue() {
            return !this.ostrich.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.ostrich.world, this.targetPos);
        }

        public boolean canStart() {
            if (this.ostrich.isBaby() && !this.ostrich.isTouchingWater()) {
                return super.canStart();
            } else {
                return !this.ostrich.isLandBound() && !this.ostrich.isTouchingWater() && !this.ostrich.hasEgg() ? super.canStart() : false;
            }
        }

        public boolean shouldResetPath() {
            return this.tryingTime % 160 == 0;
        }

        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isOf(Blocks.WATER);
        }
    }

    static class GoHomeGoal extends Goal {
        private final OstrichEntity ostrich;
        private final double speed;
        private boolean noPath;
        private int homeReachingTryTicks;
        private static final int MAX_TRY_TICKS = 600;

        GoHomeGoal(OstrichEntity ostrich, double speed) {
            this.ostrich = ostrich;
            this.speed = speed;
        }

        public boolean canStart() {
            if (this.ostrich.isBaby()) {
                return false;
            } else if (this.ostrich.hasEgg()) {
                return true;
            } else if (this.ostrich.getRandom().nextInt(toGoalTicks(700)) != 0) {
                return false;
            } else {
                return !this.ostrich.getHomePos().isWithinDistance(this.ostrich.getPos(), 64.0);
            }
        }

        public void start() {
            this.ostrich.setLandBound(true);
            this.noPath = false;
            this.homeReachingTryTicks = 0;
        }

        public void stop() {
            this.ostrich.setLandBound(false);
        }

        public boolean shouldContinue() {
            return !this.ostrich.getHomePos().isWithinDistance(this.ostrich.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= this.getTickCount(600);
        }

        public void tick() {
            BlockPos blockPos = this.ostrich.getHomePos();
            boolean bl = blockPos.isWithinDistance(this.ostrich.getPos(), 16.0);
            if (bl) {
                ++this.homeReachingTryTicks;
            }

            if (this.ostrich.getNavigation().isIdle()) {
                Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
                Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.ostrich, 16, 3, vec3d, 0.3141592741012573);
                if (vec3d2 == null) {
                    vec3d2 = NoPenaltyTargeting.findTo(this.ostrich, 8, 7, vec3d, 1.5707963705062866);
                }

                if (vec3d2 != null && !bl && !this.ostrich.world.getBlockState(new BlockPos(vec3d2)).isOf(Blocks.WATER)) {
                    vec3d2 = NoPenaltyTargeting.findTo(this.ostrich, 16, 5, vec3d, 1.5707963705062866);
                }

                if (vec3d2 == null) {
                    this.noPath = true;
                    return;
                }

                this.ostrich.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }

        }
    }

    private static class TravelGoal extends Goal {
        private final OstrichEntity ostrich;
        private final double speed;
        private boolean noPath;

        TravelGoal(OstrichEntity ostrich, double speed) {
            this.ostrich = ostrich;
            this.speed = speed;
        }

        public boolean canStart() {
            return !this.ostrich.isLandBound() && !this.ostrich.hasEgg() && this.ostrich.isTouchingWater();
        }

        public void start() {
            boolean i = true;
            boolean j = true;
            Random random = this.ostrich.random;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int m = random.nextInt(1025) - 512;
            if ((double)l + this.ostrich.getY() > (double)(this.ostrich.world.getSeaLevel() - 1)) {
                l = 0;
            }

            BlockPos blockPos = new BlockPos((double)k + this.ostrich.getX(), (double)l + this.ostrich.getY(), (double)m + this.ostrich.getZ());
            this.ostrich.setTravelPos(blockPos);
            this.ostrich.setActivelyTraveling(true);
            this.noPath = false;
        }

        public void tick() {
            if (this.ostrich.getNavigation().isIdle()) {
                Vec3d vec3d = Vec3d.ofBottomCenter(this.ostrich.getTravelPos());
                Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.ostrich, 16, 3, vec3d, 0.3141592741012573);
                if (vec3d2 == null) {
                    vec3d2 = NoPenaltyTargeting.findTo(this.ostrich, 8, 7, vec3d, 1.5707963705062866);
                }

                if (vec3d2 != null) {
                    int i = MathHelper.floor(vec3d2.x);
                    int j = MathHelper.floor(vec3d2.z);
                    boolean k = true;
                    if (!this.ostrich.world.isRegionLoaded(i - 34, j - 34, i + 34, j + 34)) {
                        vec3d2 = null;
                    }
                }

                if (vec3d2 == null) {
                    this.noPath = true;
                    return;
                }

                this.ostrich.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }

        }

        public boolean shouldContinue() {
            return !this.ostrich.getNavigation().isIdle() && !this.noPath && !this.ostrich.isLandBound() && !this.ostrich.isInLove() && !this.ostrich.hasEgg();
        }

        public void stop() {
            this.ostrich.setActivelyTraveling(false);
            super.stop();
        }
    }

    static class WanderOnLandGoal extends WanderAroundGoal {
        private final OstrichEntity ostrich;

        WanderOnLandGoal(OstrichEntity ostrich, double speed, int chance) {
            super(ostrich, speed, chance);
            this.ostrich = ostrich;
        }

        public boolean canStart() {
            return !this.mob.isTouchingWater() && !this.ostrich.isLandBound() && !this.ostrich.hasEgg() ? super.canStart() : false;
        }
    }

    static class TurtleSwimNavigation extends AxolotlSwimNavigation {
        TurtleSwimNavigation(OstrichEntity owner, World world) {
            super(owner, world);
        }

        public boolean isValidPosition(BlockPos pos) {
            MobEntity var3 = this.entity;
            if (var3 instanceof OstrichEntity ostrichEntity) {
                if (ostrichEntity.isActivelyTraveling()) {
                    return this.world.getBlockState(pos).isOf(Blocks.WATER);
                }
            }

            return !this.world.getBlockState(pos.down()).isAir();
        }
    }

}
