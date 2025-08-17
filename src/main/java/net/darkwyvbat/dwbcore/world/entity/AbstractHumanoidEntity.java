package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.tag.DwbItemTags;
import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.util.time.Cooldown;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public abstract class AbstractHumanoidEntity extends PerceptionBasedMob implements Crouchable {

    public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0, 0.6, 0.0);

    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F)
            .withEyeHeight(1.62F)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
    public static final EntityDimensions SITTING_DIMENSIONS = STANDING_DIMENSIONS.scale(1.0F, 0.7F);
    public static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F);
    public static final EntityDimensions SMALL_HITBOX_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.6F)
            .withEyeHeight(0.4F);
    public static final EntityDimensions CROUCHING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.5F)
            .withEyeHeight(1.27F)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
    public static final EntityDimensions DYING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F)
            .withEyeHeight(1.62F);

    private static final EntityDataAccessor<Integer> DATA_MOB_STATE = SynchedEntityData.defineId(AbstractHumanoidEntity.class, EntityDataSerializers.INT);

    public static final ResourceLocation SHIELD_ATTRIBUTE_ID = INFO.idOf("shield");
    public static final AttributeModifier SHIELD_KNOCKBACK_RESISTANCE = new AttributeModifier(SHIELD_ATTRIBUTE_ID, 0.8, AttributeModifier.Operation.ADD_VALUE);

    protected Cooldown useItemCD = new Cooldown(0);

    protected AbstractHumanoidEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MOB_STATE, MobStates.STANDING.getValue());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("MobState", entityData.get(DATA_MOB_STATE));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setMobState(MobStates.fromInt(valueInput.getIntOr("MobState", 0)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateState(getMobState());
        this.updateSwingTime();
    }

    public MobStates getMobState() {
        return MobStates.fromInt(entityData.get(DATA_MOB_STATE));
    }

    public void setMobState(MobStates state) {
        entityData.set(DATA_MOB_STATE, state.getValue());
        this.updateState(getMobState());
    }

    public void updateState(MobStates state) {
        switch (state) {
            case STANDING -> setPose(Pose.STANDING);
            case CROUCHING -> setPose(Pose.CROUCHING);
            case SITTING -> setPose(Pose.SITTING);
            case SWIMMING -> setPose(Pose.SWIMMING);
            default -> setPose(Pose.STANDING);
        }
    }

    public void standUpIfSitting() {
        if (this.getMobState() == MobStates.SITTING)
            setMobState(MobStates.STANDING);
    }

    @Override
    public boolean canCrouch() {
        return true;
    }

    @Override
    public boolean isCrouching() {
        return getMobState() == MobStates.CROUCHING;
    }

    @Override
    public void setCrouch(boolean crouching) {
        if (crouching)
            setMobState(MobStates.CROUCHING);
        else if (getMobState() == MobStates.CROUCHING) setMobState(MobStates.STANDING);
    }

    @Override
    public EntityDimensions getCrouchDimension() {
        return getDefaultDimensions(Pose.CROUCHING);
    }


    @Override
    public void setSwimming(boolean swimming) {
        super.setSwimming(swimming);
        if (swimming)
            setMobState(MobStates.SWIMMING);
        else if (getMobState() == MobStates.SWIMMING)
            setMobState(MobStates.STANDING);
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide)
            this.setSwimming(this.isEffectiveAi() && this.isUnderWater());
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    public Cooldown getUseItemCD() {
        return useItemCD;
    }

    @Override
    public void startUsingItem(InteractionHand hand) {
        super.startUsingItem(hand);
        ItemStack itemInHand = this.getItemInHand(hand);
        if (!this.level().isClientSide && itemInHand.is(Items.SHIELD))
            applyShieldEffects();
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        if (!this.level().isClientSide)
            removeShieldEffects();
    }

    @Override
    protected void completeUsingItem() {
        ItemStack item = this.getUseItem();
        FoodProperties foodPropertiesItem = item.get(DataComponents.FOOD);
        super.completeUsingItem();

        if (foodPropertiesItem != null && !this.level().isClientSide()) {
            this.heal(foodPropertiesItem.nutrition());
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, this.getSoundSource(), 0.5F, PoorRandom.quickFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        boolean pass = super.doHurtTarget(serverLevel, entity);
        if (pass && entity instanceof LivingEntity livingEntity)
            this.getWeaponItem().hurtAndBreak(1, livingEntity, InteractionHand.MAIN_HAND);
        return pass;
    }

    @Override
    public void hurtArmor(DamageSource damageSource, float f) {
        this.doHurtEquipment(damageSource, f, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }

    @Override
    public void onEquippedItemBroken(Item item, EquipmentSlot equipmentSlot) {
        if (item.equals(Items.SHIELD))
            removeShieldEffects();
        super.onEquippedItemBroken(item, equipmentSlot);
    }

    protected void spawnThrownItem(ItemStack itemStack, Vec3 dir, float f, int delay) {
        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getEyeY(), this.getZ(), itemStack);
        itemEntity.setThrower(this);
        itemEntity.setDeltaMovement(dir);
        itemEntity.setPickUpDelay(delay);
        this.level().addFreshEntity(itemEntity);
    }

    public void applyShieldEffects() {
        if (!getAttribute(Attributes.KNOCKBACK_RESISTANCE).hasModifier(SHIELD_ATTRIBUTE_ID))
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addTransientModifier(SHIELD_KNOCKBACK_RESISTANCE);
    }

    public void removeShieldEffects() {
        if (getAttribute(Attributes.KNOCKBACK_RESISTANCE).hasModifier(SHIELD_ATTRIBUTE_ID))
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).removeModifier(SHIELD_KNOCKBACK_RESISTANCE);
    }

    @Override
    protected void blockUsingItem(ServerLevel serverLevel, LivingEntity livingEntity) {
        ItemStack itemStack = this.getItemBlockingWith();
        BlocksAttacks blocksAttacks = itemStack != null ? itemStack.get(DataComponents.BLOCKS_ATTACKS) : null;
        float f = livingEntity.getSecondsToDisableBlocking();
        if (f > 0.0F && blocksAttacks != null) {
            useItemCD.set((int) (f * 20));
            blocksAttacks.disable(serverLevel, this, f, itemStack);
        }
    }

    @Override
    public float applyItemBlocking(ServerLevel serverLevel, DamageSource damageSource, float f) {
        ItemStack item = getItemBlockingWith();
        if (item != null && item.isDamageableItem())
            item.hurtAndBreak((int) f, this, getUsedItemHand());
        return super.applyItemBlocking(serverLevel, damageSource, f);
    }

    public boolean isFullHealth() {
        return getHealth() >= getMaxHealth();
    }

    //TODO maybe combat
    public float getHealthPercent() {
        return this.getHealth() / this.getMaxHealth();
    }

    @Override
    public float getSpeed() {
        double speed = getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (isCrouching()) speed *= 0.5;
        if (isUsingItem()) speed *= 0.7;
        return (float) speed;
    }

    protected float getDimScale() {
        return 1.0F;
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimension = switch (pose) {
            case SITTING -> SITTING_DIMENSIONS;
            case SLEEPING -> SLEEPING_DIMENSIONS;
            case FALL_FLYING, SWIMMING, SPIN_ATTACK -> SMALL_HITBOX_DIMENSIONS;
            case CROUCHING -> CROUCHING_DIMENSIONS;
            case DYING -> DYING_DIMENSIONS;
            default -> STANDING_DIMENSIONS;
        };
        return dimension.scale(getDimScale());
    }

    public boolean holdRangedWeapon() {
        return getItemInHand(InteractionHand.MAIN_HAND).is(DwbItemTags.RANGED_WEAPONS);
    }

    public abstract void disarm();

    public abstract void freeHands();

}