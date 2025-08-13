package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.Cooldown;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatConfig;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatCooldowns;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

// WIP
public class HumanoidCombatGoal extends Goal {
    public static final int STRATEGY_UPDATE_INTERVAL = 10;
    protected final AbstractInventoryHumanoid mob;
    private LivingEntity target;

    private final Cooldown strategyCD = new Cooldown();
    public CombatState state;
    public CombatConfig config;
    public CombatCooldowns cooldowns = new CombatCooldowns();

    CombatStrategy meleeStrategy = new MeleeStrategy();
    private final List<CombatStrategy> strategies = List.of(
            new HealStrategy(),
            new PotionStrategy(),
            new KitingStrategy(),
            new RangedStrategy(),
            meleeStrategy
    );
    public CombatStrategy currentStrategy;
    private Optional<CombatStrategy> prevStrategy = Optional.empty();

    public HumanoidCombatGoal(AbstractInventoryHumanoid mob, CombatConfig config) {
        this.mob = mob;
        this.config = config;
        this.state = new CombatState(mob, config, cooldowns);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || mob.getAirSupply() < 10) return false;
        if (target instanceof Player p && (p.isSpectator() || p.isCreative())) return false;
        this.target = target;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || this.target != target || mob.getAirSupply() < 5) return false;
        return !(target instanceof Player p) || !(p.isSpectator() || p.isCreative());
    }

    @Override
    public void start() {
        super.start();
        mob.setAggressive(true);
        target = mob.getTarget();
        cooldowns.reset();
        strategyCD.reset();
    }

    @Override
    public void stop() {
        super.stop();
        LivingEntity target = mob.getTarget();
        if ((target instanceof Player p && (p.isSpectator() || p.isCreative())) || (target != null && !target.isAlive()))
            mob.setTarget(null);
        mob.setAggressive(false);
        this.target = null;
        mob.getNavigation().stop();
        mob.stopUsingItem();
        mob.shouldConsumeNow = false;
    }

    @Override
    public void tick() {
        target = mob.getTarget();
        if (target == null || !target.isAlive()) return;

        mob.getLookControl().setLookAt(target);
        double dist = mob.distanceToSqr(target);
        boolean see = mob.getSensing().hasLineOfSight(target);
        int seeTime = see ? Math.min(state.getSeeTime() + 1, 30) : Math.max(state.getSeeTime() - 1, -10);
        state.setRetreating(false);
        state.setTarget(target);
        state.setDistanceSqr(dist);
        state.setSeeTime(seeTime);
        state.setCanSeeTarget(see);
        state.setPrevStrategy(currentStrategy);
        cooldowns.tick();

        if (strategyCD.tick()) {
            CombatStrategy newStrategy = strategies.stream().filter(s -> s.canStart(state)).findFirst().orElse(meleeStrategy);
            if (changeStrategy(newStrategy)) {
                state.setNextStrategy(newStrategy);
                prevStrategy.ifPresent(s -> s.stop(state));
                this.currentStrategy.start(state);
                strategyCD.set(STRATEGY_UPDATE_INTERVAL);
            }
        }
        currentStrategy.tick(state);
    }

    public boolean changeStrategy(CombatStrategy newStrategy) {
        if (newStrategy != this.currentStrategy) {
            this.prevStrategy = Optional.ofNullable(this.currentStrategy);
            this.currentStrategy = newStrategy;
            return true;
        }
        return false;
    }


    public static int getConcertedTicks(int v) {
        return v / STRATEGY_UPDATE_INTERVAL;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}