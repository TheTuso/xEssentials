package pl.tuso.essentials.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class IntelligentArmorStand extends PathfinderMob implements ArmorStandProperties {
    // LivingEntity
    private static final EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS = EntityDataSerializers.BYTE.createAccessor(8);
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID = EntityDataSerializers.FLOAT.createAccessor(9);
    private static final EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID = EntityDataSerializers.INT.createAccessor(10);
    private static final EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID = EntityDataSerializers.BOOLEAN.createAccessor(11);
    private static final EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID = EntityDataSerializers.INT.createAccessor(12);
    private static final EntityDataAccessor<Integer> DATA_STINGER_COUNT_ID = EntityDataSerializers.INT.createAccessor(13);
    private static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID = EntityDataSerializers.OPTIONAL_BLOCK_POS.createAccessor(14);
    // ArmorStand
    private static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS = EntityDataSerializers.BYTE.createAccessor(15);
    private static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = EntityDataSerializers.ROTATIONS.createAccessor(16);
    private static final EntityDataAccessor<Rotations> DATA_BODY_POSE = EntityDataSerializers.ROTATIONS.createAccessor(17);
    private static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = EntityDataSerializers.ROTATIONS.createAccessor(18);
    private static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = EntityDataSerializers.ROTATIONS.createAccessor(19);
    private static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = EntityDataSerializers.ROTATIONS.createAccessor(20);
    private static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = EntityDataSerializers.ROTATIONS.createAccessor(21);
    // Default poses
    private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(0.0F, 0.0F, 0.0F);

    private Rotations headPose;
    private Rotations bodyPose;
    private Rotations leftArmPose;
    private Rotations rightArmPose;
    private Rotations leftLegPose;
    private Rotations rightLegPose;

    public IntelligentArmorStand(EntityType<? extends IntelligentArmorStand> type, Level world) {
        super(type, world);
        this.headPose = DEFAULT_HEAD_POSE;
        this.bodyPose = DEFAULT_BODY_POSE;
        this.leftArmPose = DEFAULT_LEFT_ARM_POSE;
        this.rightArmPose = DEFAULT_RIGHT_ARM_POSE;
        this.leftLegPose = DEFAULT_LEFT_LEG_POSE;
        this.rightLegPose = DEFAULT_RIGHT_LEG_POSE;
    }

    @Override
    public void defineSynchedData() {
        // LivingEntity
        this.entityData.define(DATA_LIVING_ENTITY_FLAGS, (byte) 0);
        this.entityData.define(DATA_EFFECT_COLOR_ID, 0);
        this.entityData.define(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.define(DATA_ARROW_COUNT_ID, 0);
        this.entityData.define(DATA_STINGER_COUNT_ID, 0);
        this.entityData.define(DATA_HEALTH_ID, 1.0F);
        this.entityData.define(SLEEPING_POS_ID, Optional.empty());
        // ArmorStand
        this.entityData.define(DATA_CLIENT_FLAGS, (byte) 0);
        this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
        this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
        this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
        this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
        this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
        this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
    }

    @Override
    public void registerGoals() {
        // Pathfinders
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isNoAi() {
        return false; // setSmall uses the same bit field
    }

    @Override
    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);
        this.persistentInvisibility = invisible;
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void setSmall(boolean small) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, small));
    }

    @Override
    public boolean isSmall() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
    }

    @Override
    public void setShowArms(boolean showArms) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, showArms));
    }

    @Override
    public boolean isShowArms() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
    }

    @Override
    public void setNoBasePlate(boolean hideBasePlate) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, hideBasePlate));
    }

    @Override
    public boolean isNoBasePlate() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
    }

    @Override
    public void setMarker(boolean marker) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, marker));
    }

    @Override
    public boolean isMarker() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
    }

    @Override
    public void setHeadPose(Rotations angle) {
        this.headPose = angle;
        this.entityData.set(DATA_HEAD_POSE, angle);
    }

    @Override
    public void setBodyPose(Rotations angle) {
        this.bodyPose = angle;
        this.entityData.set(DATA_BODY_POSE, angle);
    }

    @Override
    public void setLeftArmPose(Rotations angle) {
        this.leftArmPose = angle;
        this.entityData.set(DATA_LEFT_ARM_POSE, angle);
    }

    @Override
    public void setRightArmPose(Rotations angle) {
        this.rightArmPose = angle;
        this.entityData.set(DATA_RIGHT_ARM_POSE, angle);
    }

    @Override
    public void setLeftLegPose(Rotations angle) {
        this.leftLegPose = angle;
        this.entityData.set(DATA_LEFT_LEG_POSE, angle);
    }

    @Override
    public void setRightLegPose(Rotations angle) {
        this.rightLegPose = angle;
        this.entityData.set(DATA_RIGHT_LEG_POSE, angle);
    }

    @Override
    public Rotations getHeadPose() {
        return this.headPose;
    }

    @Override
    public Rotations getBodyPose() {
        return this.bodyPose;
    }

    @Override
    public Rotations getLeftArmPose() {
        return this.leftArmPose;
    }

    @Override
    public Rotations getRightArmPose() {
        return this.rightArmPose;
    }

    @Override
    public Rotations getLeftLegPose() {
        return this.leftLegPose;
    }

    @Override
    public Rotations getRightLegPose() {
        return this.rightLegPose;
    }

    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    private byte setBit(byte value, int bitField, boolean set) {
        if (set) {
            value = (byte) (value | bitField);
        } else {
            value = (byte) (value & ~bitField);
        }
        return value;
    }
}