package net.darkwyvbat.dwbcore.debug;

import net.darkwyvbat.dwbcore.util.time.Timeline;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.CombatantInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.GrowableMob;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategyManager;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.DwbCombatConfigs;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy.*;
import net.darkwyvbat.dwbcore.world.entity.ai.goal.*;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.HumanoidLikeMoveControl;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.HumanoidLikePathNavigation;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.DwbOpinions;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.Reputation;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.PerceptionCenter;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.PerceptionProfile;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HumanoidTester extends CombatantInventoryHumanoid implements GrowableMob<HumanoidTester> {

    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(HumanoidTester.class, EntityDataSerializers.BOOLEAN);
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(ResourceLocation.withDefaultNamespace("baby"), 0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    private final Timeline<HumanoidTester> timeline = new Timeline<>(this);

    public HumanoidTester(EntityType<? extends HumanoidTester> entityType, Level level) {
        super(entityType, level);
        moveControl = new HumanoidLikeMoveControl(this);
        navigation = new HumanoidLikePathNavigation(this, level);
        defineTimeline();
        timeline.init();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BABY_ID, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHumanoidEntity.createHumanoidAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 0.8F)
                .add(Attributes.SAFE_FALL_DISTANCE, 3.0)
                .add(Attributes.SCALE, 1.0);
    }

    @Override
    public int getGrowthDuration() {
        return 60;
    }

    @Override
    public Timeline<HumanoidTester> getTimeline() {
        return timeline;
    }

    @Override
    public void onGrow() {
        System.out.println("blyat");
    }

    @Override
    protected float getDimScale() {
        return isBaby() ? 0.5F : super.getDimScale();
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        tickTimeline();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        CombatStrategyManager strategyManager = CombatStrategyManager.builder()
                .add(new HealStrategy(this))
                .add(new PotionAttackStrategy(this))
                .add(new KitingStrategy(this))
                .add(new RangedStrategy(this))
                .defaultStrategy(new MeleeStrategy(this))
                .changeInterval(10)
                .build();
        HumanoidCombatGoal combatGoal = new HumanoidCombatGoal(this, DwbCombatConfigs.HUMANOID_BASE_CONFIG, strategyManager);
        this.goalSelector.addGoal(1, new OpenPassageGoal(this));
        this.goalSelector.addGoal(2, combatGoal);
        this.goalSelector.addGoal(3, new GoToWantedItemGoal(this, 1.2));
        this.goalSelector.addGoal(4, new GoToGoodLandGoal(this, 1.0));
        this.goalSelector.addGoal(5, new HumanoidGoalsCollection.BeOnGuardGoal(this));
        this.goalSelector.addGoal(5, new RandomWalkGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtEntityGoal(this, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new ConsumeGoal(this));
        this.goalSelector.addGoal(8, new HumanoidGoalsCollection.RandomFreeHandsGoal(this));
        this.goalSelector.addGoal(9, new HumanoidGoalsCollection.RandomDisarm(this, 100));
        this.goalSelector.addGoal(10, new HumanoidGoalsCollection.OptimizeInventory(this));

        this.targetSelector.addGoal(1, new RevengeTargetGoal(this, e -> !(e instanceof HumanoidTester)));
        this.targetSelector.addGoal(2, new AttackBadTargetGoal(this, Reputation.DISLIKED));
    }

    @Override
    public PerceptionCenter createPerception() {
        return new PerceptionCenter(this, new PerceptionProfile(5, 10), DwbOpinions._HUMAN_TESTER_OPINIONS);
    }

    @Override
    public void setBaby(boolean bl) {
        this.getEntityData().set(DATA_BABY_ID, bl);
        if (!this.level().isClientSide) {
            AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            attributeInstance.removeModifier(SPEED_MODIFIER_BABY.id());
            if (bl) {
                attributeInstance.addTransientModifier(SPEED_MODIFIER_BABY);
            }
        }
    }

    @Override
    public boolean isBaby() {
        return this.getEntityData().get(DATA_BABY_ID);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_BABY_ID.equals(entityDataAccessor))
            this.refreshDimensions();
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("IsBaby", this.isBaby());
        saveChronology(valueOutput);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setBaby(valueInput.getBooleanOr("IsBaby", false));
        loadChronology(valueInput);
    }
}