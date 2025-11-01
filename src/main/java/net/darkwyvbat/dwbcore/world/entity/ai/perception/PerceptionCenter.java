package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import net.darkwyvbat.dwbcore.world.entity.EntityUtils;
import net.darkwyvbat.dwbcore.world.entity.PerceptionBasedMob;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.Opinion;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.OpinionResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PerceptionCenter {
    private final PerceptionBasedMob mob;
    private PerceptionProfile profile;
    private final OpinionResolver opinions;
    private SensoryInput lastScan;
    private boolean isChasing;

    public PerceptionCenter(PerceptionBasedMob mob, PerceptionProfile profile, OpinionResolver opinions) {
        this.mob = mob;
        this.profile = profile;
        this.opinions = opinions;
        this.lastScan = new SensoryInput(List.of(), List.of(), Optional.empty(), Optional.empty(), 0);
    }

    public void read(ValueInput in) {
        in.read("perception_profile", PerceptionProfile.CODEC).ifPresent(loadedProfile -> profile = loadedProfile);
    }

    public void write(ValueOutput out) {
        out.store("perception_profile", PerceptionProfile.CODEC, profile);
    }

    public void tick() {
        this.isChasing = mob.getTarget() != null;
        updateActivity();

        if (mob.tickCount % 16 == 0)
            lazyTick();
    }

    public void lazyTick() {
        this.lastScan = scanWorld();
        processSensoryInput(this.lastScan);
    }

    private void updateActivity() {
        if (profile.getActivityLevel() > 0) profile.reduceActivity(1);
        if (mob.getRandom().nextFloat() < 0.1F) profile.addActivityLevel(5);
        if (this.isChasing) profile.addActivityIfLess(20, 20);
        if (mob.isInWater()) profile.addActivityIfLess(10, 10);
        if (mob.hurtTime > 0) profile.addActivityIfLess(30, 20);
    }

    private SensoryInput scanWorld() {
        List<Entity> entitiesAround = EntityUtils.getEntitiesInAABB(mob, 16);
        List<ItemEntity> itemsAround = new ArrayList<>();
        Optional<Entity> poiEntity, dangerestEntity;

        Entity currentDangerestEntity = null, currentInterestestEntity = null;
        int maxDanger = 0, maxInterest = -1, dangerAround = 0;
        for (Entity entity : entitiesAround) {
            if (entity.equals(mob)) continue;
            if (entity instanceof ItemEntity ie) itemsAround.add(ie);
            Opinion opinion = opinions.get(entity.getClass());
            if (opinion.interestLevel().getValue() > maxInterest) {
                maxInterest = opinion.interestLevel().getValue();
                currentInterestestEntity = entity;
            }
            if (opinion.dangerLevel().getValue() > maxDanger) {
                maxDanger = opinion.dangerLevel().getValue();
                currentDangerestEntity = entity;
            }
            dangerAround += opinion.dangerLevel().getValue();
        }
        poiEntity = Optional.ofNullable(currentInterestestEntity);
        dangerestEntity = Optional.ofNullable(currentDangerestEntity);

        return new SensoryInput(entitiesAround, itemsAround, poiEntity, dangerestEntity, dangerAround);
    }

    public boolean isChasing() {
        return this.isChasing;
    }

    private void processSensoryInput(SensoryInput input) {
        profile.setDangerLevel(input.dangerAround());
    }

    public SensoryInput getLastScan() {
        return this.lastScan;
    }

    public PerceptionProfile getProfile() {
        return this.profile;
    }

    public OpinionResolver getOpinions() {
        return opinions;
    }
}
