package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.world.entity.ai.opinion.OpinionResolver;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.PerceptionCenter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class PerceptionBasedMob extends PathfinderMob {

    protected PerceptionCenter perception;

    protected PerceptionBasedMob(EntityType<? extends PerceptionBasedMob> entityType, Level level) {
        super(entityType, level);
        perception = createPerception();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        if (this.perception != null)
            this.perception.write(valueOutput);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        perception.read(valueInput);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        perception.tick();
    }

    public abstract PerceptionCenter createPerception();

    public PerceptionCenter getPerception() {
        return this.perception;
    }

    public OpinionResolver getOpinions() {
        return perception.getOpinions();
    }
}