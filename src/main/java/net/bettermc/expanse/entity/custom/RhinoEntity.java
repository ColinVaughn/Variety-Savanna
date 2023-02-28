package net.bettermc.expanse.entity.custom;

import net.bettermc.expanse.entity.ModEntities;
import net.bettermc.expanse.util.ConfigUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.MathHelper;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.UUID;


public class RhinoEntity extends AnimalEntity implements Angerable, IAnimatable {

    private static final TrackedData<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    private static final Ingredient LOVINGFOOD;
    private int angerTime;
    private UUID targetUuid;

    private static double health = ConfigUtil.INSTANCE.getDoubleProperty("entity.health");
    private static double speed = ConfigUtil.INSTANCE.getDoubleProperty("entity.speed");
    private static double follow = ConfigUtil.INSTANCE.getDoubleProperty("entity.follow");
    private static double damage = ConfigUtil.INSTANCE.getDoubleProperty("entity.damage");
    private static int angermin = ConfigUtil.INSTANCE.getNumberProperty("entity.angertimemin");
    private static int angermax = ConfigUtil.INSTANCE.getNumberProperty("entity.angertimemax");
    private static boolean friendly = ConfigUtil.INSTANCE.getBooleanProperty("entity.friendlytoplayer");
    private AnimationFactory factory = new AnimationFactory(this);

    public RhinoEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, health)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 200)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, follow)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, damage)
                .add(EntityAttributes.GENERIC_ARMOR,5);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new RhinoEntity.AttackGoal());
        this.goalSelector.add(1, new RhinoEscapeDangerGoal());
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, LOVINGFOOD, false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RhinoRevengeGoal());
        if (!friendly) {
            this.targetSelector.add(2, new RhinoEntity.ProtectBabiesGoal());
            this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
            this.targetSelector.add(4, new ActiveTargetGoal<>(this, FoxEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new ActiveTargetGoal<>(this, RabbitEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new ActiveTargetGoal<>(this, ChickenEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new ActiveTargetGoal<>(this, BeeEntity.class, 10, true, true, null));
            this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
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
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.WHEAT;
    }
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.RHINO.create(world);
    }
    // ANIMATIONS
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.dead){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.death", true));
            return PlayState.CONTINUE;
        }

        if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.attack", true));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rhino.idle", true));
        return PlayState.CONTINUE;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.world, nbt);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }


    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngryAt(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    public UUID getAngryAt() {
        return this.targetUuid;
    }



    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            //this.playSound(Main.RHINO_WARNING, 1.0F, this.getSoundPitch());
            this.warningSoundCooldown = 40;
        }

    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WARNING, false);
    }

    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
                this.calculateDimensions();
            }

            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            if (this.isWarning()) {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress + 1.0F, 0.0F, 6.0F);
            } else {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundCooldown > 0) {
            --this.warningSoundCooldown;
        }

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }

    }

    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.warningAnimationProgress > 0.0F) {
            float f = this.warningAnimationProgress / 6.0F;
            float g = 1.0F + f;
            return super.getDimensions(pose).scaled(1.0F, g);
        } else {
            return super.getDimensions(pose);
        }
    }


    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        if (bl) {
            this.applyDamageEffects(this, target);
        }

        return bl;
    }



    public boolean isWarning() {
        return this.dataTracker.get(WARNING);
    }

    public void setWarning(boolean warning) {
        this.dataTracker.set(WARNING, warning);
    }

    @Environment(EnvType.CLIENT)
    public float getWarningAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0F;
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.98F;
    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (entityData == null) {
            entityData = new PassiveData(1.0F);
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    static {
        WARNING = DataTracker.registerData(RhinoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
        LOVINGFOOD = Ingredient.ofItems(Items.COD, Items.SALMON, Items.SWEET_BERRIES);
    }

    class RhinoEscapeDangerGoal extends EscapeDangerGoal {
        public RhinoEscapeDangerGoal() {
            super(RhinoEntity.this, 2.0D);
        }

        public boolean canStart() {
            return (RhinoEntity.this.isBaby() || RhinoEntity.this.isOnFire()) && super.canStart();
        }
    }
        private class AttackGoal extends MeleeAttackGoal {
            public AttackGoal() {
                super(RhinoEntity.this, 1.25, true);
            }

            protected void attack(LivingEntity target, double squaredDistance) {
                double d = this.getSquaredMaxAttackDistance(target);
                if (squaredDistance <= d && this.isCooledDown()) {
                    this.resetCooldown();
                    this.mob.tryAttack(target);
                    RhinoEntity.this.setWarning(false);
                } else if (squaredDistance <= d * 2.0) {
                    if (this.isCooledDown()) {
                        RhinoEntity.this.setWarning(false);
                        this.resetCooldown();
                    }

                    if (this.getCooldown() <= 10) {
                        RhinoEntity.this.setWarning(true);
                        RhinoEntity.this.playWarningSound();
                    }
                } else {
                    this.resetCooldown();
                    RhinoEntity.this.setWarning(false);
                }

            }

            public void stop() {
                RhinoEntity.this.setWarning(false);
                super.stop();
            }

            protected double getSquaredMaxAttackDistance(LivingEntity entity) {
                return (double)(4.0F + entity.getWidth());
            }
        }

    class RhinoRevengeGoal extends RevengeGoal {
        public RhinoRevengeGoal() {
            super(RhinoEntity.this);
        }

        public void start() {
            super.start();
            if (RhinoEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }

        }

        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof RhinoEntity && !mob.isBaby()) {
                super.setMobEntityTarget(mob, target);
            }

        }
    }

    class ProtectBabiesGoal extends ActiveTargetGoal<PlayerEntity> {
        public ProtectBabiesGoal() {
            super(RhinoEntity.this, PlayerEntity.class, 20, true, true, null);
        }

        public boolean canStart() {
            if (!RhinoEntity.this.isBaby()) {
                if (super.canStart()) {
                    List<RhinoEntity> list = RhinoEntity.this.world.getNonSpectatingEntities(RhinoEntity.class, RhinoEntity.this.getBoundingBox().expand(8.0D, 4.0D, 8.0D));

                    for (RhinoEntity rhinoEntity : list) {
                        if (rhinoEntity.isBaby()) {
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        protected double getFollowRange() {
            return super.getFollowRange() * 0.5D;
        }
    }
}