package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class GoToGoodLandGoal extends MoveToBlockGoal {

    public GoToGoodLandGoal(AbstractHumanoidEntity entity, double d) {
        super(entity, d, 16, 8);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.mob.isInWater() || mob.getAirSupply() < 5;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockPos pos = blockPos.above();
        return levelReader.isEmptyBlock(pos) && levelReader.isEmptyBlock(pos.above()) && levelReader.getBlockState(blockPos).entityCanStandOn(levelReader, blockPos, this.mob);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
