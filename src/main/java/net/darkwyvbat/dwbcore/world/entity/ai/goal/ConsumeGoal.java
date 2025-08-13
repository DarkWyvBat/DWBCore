package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.Cooldown;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class ConsumeGoal extends Goal {
    protected final AbstractInventoryHumanoid mob;
    protected ItemStack item;
    protected Cooldown cd = new Cooldown(20);
    boolean needHeal, isDrowning, isBurning;

    public ConsumeGoal(AbstractInventoryHumanoid mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (!cd.tick()) return false;

        needHeal = !mob.isFullHealth();
        isDrowning = (double) mob.getAirSupply() / mob.getMaxAirSupply() < 0.3;
        isBurning = mob.isOnFire();

        if ((needHeal || isDrowning || isBurning) && !mob.isUsingItem() && !mob.isAggressive() || mob.shouldConsumeNow) {
            int item;
            if (isBurning && !mob.hasEffect(MobEffects.FIRE_RESISTANCE))
                item = mob.getInventoryManager().getPotionWithEffectIndex(MobEffects.FIRE_RESISTANCE);
            else if (isDrowning)
                item = mob.getInventoryManager().getPotionWithEffectIndex(MobEffects.WATER_BREATHING);
            else
                item = mob.getInventoryManager().getForHealIndex();
            mob.equipFromInventory(EquipmentSlot.OFFHAND, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        mob.stopUsingItem();
        mob.startUsingItem(InteractionHand.OFF_HAND);
        cd.reset();
    }
}