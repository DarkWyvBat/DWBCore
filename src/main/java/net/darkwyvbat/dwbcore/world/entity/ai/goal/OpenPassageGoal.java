package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import com.google.common.collect.Sets;
import net.darkwyvbat.dwbcore.tag.DwbBlockTags;
import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OpenPassageGoal extends Goal {
    private static final int OPEN_CD = 10;
    private static final double CLOSE_DIST = 1.7;
    private static final double HOLD_DIST = 2.0;

    protected final Mob mob;
    private final Set<GlobalPos> toClose = Sets.newHashSet();
    private final TickingCooldown cd = new TickingCooldown(0);

    public OpenPassageGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.noneOf(Flag.class));
    }

    private boolean isOpen(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof DoorBlock doorBlock) return doorBlock.isOpen(state);
        if (block instanceof FenceGateBlock) return state.getValue(FenceGateBlock.OPEN);
        return false;
    }

    private void setState(ServerLevel level, BlockState state, BlockPos pos, boolean open, Mob mob) {
        boolean stateChanged = false;
        Block block = state.getBlock();

        if (block instanceof DoorBlock door) {
            if (door.isOpen(state) != open) {
                door.setOpen(mob, level, state, pos, open);
                stateChanged = true;
            }
        } else if (block instanceof FenceGateBlock) {
            if (state.getValue(FenceGateBlock.OPEN) != open) {
                level.setBlock(pos, state.setValue(FenceGateBlock.OPEN, open), Block.UPDATE_ALL);
                level.playSound(null, pos, open ? SoundEvents.FENCE_GATE_OPEN : SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent(mob, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
                stateChanged = true;
            }
        }
        if (stateChanged)
            mob.swing(InteractionHand.MAIN_HAND);
    }

    private boolean isClosed(BlockState state) {
        return state.is(DwbBlockTags.MOB_INTERACTABLE_PASSAGES) && !isOpen(state);
    }

    @Nullable
    private BlockPos getPrev(Path path) {
        Node n = path.getPreviousNode();
        return n != null ? n.asBlockPos() : null;
    }

    @Override
    public boolean canUse() {
        if (!mob.getNavigation().isDone()) {
            Path path = mob.getNavigation().getPath();
            if (path == null || path.isDone()) return false;

            for (int i = path.getNextNodeIndex(); i < path.getNodeCount(); ++i) {
                BlockPos nodePos = path.getNode(i).asBlockPos();
                if (isClosed(mob.level().getBlockState(nodePos)) || mob.getBbHeight() > 1.0F && isClosed(mob.level().getBlockState(nodePos.above())))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !cd.isReady() || !toClose.isEmpty();
    }

    @Override
    public void start() {
        cd.reset();
    }

    @Override
    public void stop() {
        if (mob.level() instanceof ServerLevel level && !toClose.isEmpty())
            close(level, mob, null, null, true);
        toClose.clear();
        cd.reset();
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel level)) return;

        Path path = mob.getNavigation().getPath();
        if (path != null)
            close(level, mob, getPrev(path), path.isDone() ? null : path.getNextNode().asBlockPos(), false);
        else
            close(level, mob, null, null, false);

        if (!cd.tick()) return;

        boolean openedSomething = false;
        double inflationAmount = mob.getBbWidth() * (1.2 - 1.0) / 2.0;
        AABB aabb = mob.getBoundingBox().inflate(inflationAmount, 0.0, inflationAmount);

        for (BlockPos posInBox : BlockPos.betweenClosed(BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ), BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ))) {
            BlockState state = level.getBlockState(posInBox);
            if (isClosed(state)) {
                setState(level, state, posInBox, true, mob);
                remember(level, posInBox.immutable());
                openedSomething = true;
            }
        }

        if (openedSomething) cd.set(OPEN_CD);
    }

    private void remember(ServerLevel sl, BlockPos p) {
        toClose.add(GlobalPos.of(sl.dimension(), p));
    }

    private void close(ServerLevel level, LivingEntity entity, @Nullable BlockPos pp, @Nullable BlockPos np, boolean force) {
        if (toClose.isEmpty()) return;

        Iterator<GlobalPos> it = toClose.iterator();
        while (it.hasNext()) {
            GlobalPos gp = it.next();
            BlockPos bp = gp.pos();
            BlockState state = level.getBlockState(bp);
            if (!state.is(DwbBlockTags.MOB_INTERACTABLE_PASSAGES) || !isOpen(state)) {
                it.remove();
                continue;
            }
            if (!(force || isFar(level, entity, gp))) continue;
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(bp).inflate(HOLD_DIST), x -> x != entity && x.getType() == entity.getType() && x instanceof Mob);
            if (othersThrough(bp, entities)) {
                if (force)
                    it.remove();
                continue;
            }

            setState(level, state, bp, false, mob);
            it.remove();
        }
    }

    private boolean othersThrough(BlockPos blockPos, List<LivingEntity> near) {
        return near.stream().filter(x -> blockPos.closerToCenterThan(x.position(), HOLD_DIST)).anyMatch(x -> isThrough((Mob) x, blockPos));
    }

    private boolean isThrough(Mob mob, BlockPos pos) {
        Path path = mob.getNavigation().getPath();
        if (path == null || path.isDone()) return false;
        Node prev = path.getPreviousNode();
        if (prev != null && pos.equals(prev.asBlockPos())) return true;

        return pos.equals(path.getNextNode().asBlockPos());
    }

    private boolean isFar(ServerLevel level, LivingEntity entity, GlobalPos pos) {
        if (pos.dimension() != level.dimension()) return true;
        double dX = entity.getX() - (pos.pos().getX() + 0.5);
        double dZ = entity.getZ() - (pos.pos().getZ() + 0.5);
        return dX * dX + dZ * dZ > CLOSE_DIST * CLOSE_DIST;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}