package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import net.darkwyvbat.dwbcore.world.entity.EntityUtils;
import net.darkwyvbat.dwbcore.world.entity.PerceptionBasedMob;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.Opinion;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.OpinionResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Optional;

public class PerceptionCenter {
    private final PerceptionBasedMob mob;
    private PerceptionProfile profile;
    private final OpinionResolver opinions;
    private SensoryInput lastScan;

    public PerceptionCenter(PerceptionBasedMob mob, PerceptionProfile profile, OpinionResolver opinions) {
        this.mob = mob;
        this.profile = profile;
        this.opinions = opinions;
    }

    public void read(ValueInput in) {
        in.read("perception_profile", PerceptionProfile.CODEC).ifPresent(loadedProfile -> profile = loadedProfile);
    }

    public void write(ValueOutput out) {
        out.store("perception_profile", PerceptionProfile.CODEC, profile);
    }

    public void tick() {
        if (mob.tickCount % 32 == 0)
            lazyTick();
    }

    protected void lazyTick() {
        // processSensoryInput(lastScan);
    }

    public SensoryInput scanWorld() {
        return scanWorld(false);
    }

    public SensoryInput scanWorld(boolean capture) {
        List<Entity> entitiesAround = EntityUtils.getEntitiesInAABB(mob, 16);
        Optional<Entity> poiEntity, dangerestEntity;

        Entity currentDangerestEntity = null, currentInterestestEntity = null;
        int maxDanger = 0, maxInterest = -1, dangerAround = 0;
        for (Entity entity : entitiesAround) {
            if (entity.equals(mob)) continue;
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

        SensoryInput scanRes = new SensoryInput(entitiesAround, poiEntity, dangerestEntity, dangerAround);
        if (capture) lastScan = scanRes;
        return scanRes;
    }

    protected void processSensoryInput(SensoryInput input) {
        profile.setDangerLevel(input.dangerAround());
    }

    public SensoryInput getLastScan() {
        return lastScan;
    }

    public PerceptionProfile getProfile() {
        return profile;
    }

    public OpinionResolver getOpinions() {
        return opinions;
    }
}
