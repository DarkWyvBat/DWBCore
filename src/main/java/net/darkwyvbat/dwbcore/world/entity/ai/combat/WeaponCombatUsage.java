package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.darkwyvbat.dwbcore.util.MathUtils;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.EntityUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;

import java.util.HashMap;
import java.util.Map;


public final class WeaponCombatUsage {
    @FunctionalInterface
    public interface WeaponHandler {
        void handle(CombatState state, ItemStack weapon, InteractionHand hand);
    }

    private static final Map<Item, WeaponHandler> WEAPON_HANDLERS = new HashMap<>();
    public static final float PROJECTILE_ACCURACY = 1.0F;

    static {
        registerVanillaWeaponHandlers();
    }

    private static void registerVanillaWeaponHandlers() {
        WEAPON_HANDLERS.put(Items.BOW, (state, item, hand) -> {
            AbstractInventoryHumanoid mob = state.getMob();
            if (mob.isUsingItem()) {
                int ticks = mob.getTicksUsingItem();
                if ((!state.canSeeTarget() && state.getSeeTime() < state.getConfig().seeTimeStop()) || state.getDistanceSqr() > state.getConfig().rangedConfig().lostRangeSqr()) {
                    mob.stopUsingItem();
                    return;
                }
                if (state.canSeeTarget() && EntityUtils.isFirelineClear(mob, 16, e -> e.getType() == mob.getType()) && ticks > BowItem.MAX_DRAW_DURATION) {
                    mob.releaseUsingItem();
                    mob.performRangedAttack(state.getTarget(), mob.getUsedItemHand(), BowItem.getPowerForTime(ticks));
                    state.startRangedCooldown(state.getConfig().rangedConfig().cd());
                }
            } else if (state.isRangedCooldownReady() && state.canSeeTarget() && state.getSeeTime() >= 0)
                if (MathUtils.isBetween(state.getDistanceSqr(), state.getConfig().meleeConfig().maxDistSqr(), state.getConfig().rangedConfig().maxRangeSqr()))
                    mob.startUsingItem(hand);
        });

        WEAPON_HANDLERS.put(Items.CROSSBOW, (state, item, hand) -> {
            if (CrossbowItem.isCharged(item) && state.isRangedCooldownReady() && state.canSeeTarget()) {
                if (EntityUtils.isFirelineClear(state.getMob(), 16, e -> e.getType() == state.getMob().getType())) {
                    ((CrossbowItem) item.getItem()).performShooting(state.getMob().level(), state.getMob(), hand, item, 1.6f, PROJECTILE_ACCURACY, state.getMob().getTarget());
                    state.startRangedCooldown(state.getConfig().rangedConfig().cd());
                    state.getMob().stopUsingItem();
                }
                return;
            }
            if (!state.getMob().isUsingItem() && state.isRangedCooldownReady() && !CrossbowItem.isCharged(item)) {
                state.getMob().startUsingItem(hand);
                return;
            }
            if (state.getMob().getTicksUsingItem() >= CrossbowItem.getChargeDuration(item, state.getMob())) {
                item.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(Items.ARROW)));
                state.getMob().releaseUsingItem();
                state.startRangedCooldown(state.getConfig().rangedConfig().cd());
            }
        });

        WEAPON_HANDLERS.put(Items.TRIDENT, (state, item, hand) -> {
            AbstractInventoryHumanoid mob = state.getMob();
            if (mob.isUsingItem()) {
                if (!state.canSeeTarget()) {
                    mob.stopUsingItem();
                    return;
                }
                if (mob.getTicksUsingItem() > TridentItem.THROW_THRESHOLD_TIME && state.canSeeTarget() && EntityUtils.isFirelineClear(mob, 16, e -> e.getType() == mob.getType())) {
                    mob.performRangedAttack(state.getTarget(), mob.getUsedItemHand(), 1.0F);
                    state.startRangedCooldown(state.getConfig().rangedConfig().cd() * 2);
                    mob.stopUsingItem();
                }
            } else if (state.isRangedCooldownReady() && state.canSeeTarget())
                mob.startUsingItem(hand);
        });

        WEAPON_HANDLERS.put(Items.SPLASH_POTION, (state, item, hand) -> {
            if (state.isRangedCooldownReady() && state.canSeeTarget() && EntityUtils.isFirelineClear(state.getMob(), 16, e -> e.getType() == state.getMob().getType())) {
                state.getMob().performRangedAttack(state.getTarget(), hand, 1.0F);
                state.getMob().swing(hand);
                state.startRangedCooldown(10);
            }
        });

        WEAPON_HANDLERS.put(Items.LINGERING_POTION, (state, item, hand) -> {
            if (state.isRangedCooldownReady() && state.canSeeTarget() && EntityUtils.isFirelineClear(state.getMob(), 16, e -> e.getType() == state.getMob().getType())) {
                state.getMob().performRangedAttack(state.getTarget(), hand, 1.0F);
                state.getMob().swing(hand);
                state.startRangedCooldown(10);
            }
        });

        WEAPON_HANDLERS.put(Items.WIND_CHARGE, (state, item, hand) -> {
            if (state.isRangedCooldownReady() && state.canSeeTarget() && EntityUtils.isFirelineClear(state.getMob(), 16, e -> e.getType() == state.getMob().getType())) {
                state.getMob().performRangedAttack(state.getTarget(), hand, 1.0F);
                state.getMob().swing(hand);
                state.startRangedCooldown(20);
            }
        });
    }

    public static void register(Item weapon, WeaponHandler handler) {
        WEAPON_HANDLERS.put(weapon, handler);
    }

    public static void unregister(Item weapon) {
        WEAPON_HANDLERS.remove(weapon);
    }

    public static void tryRanged(CombatState state, InteractionHand hand) {
        ItemStack weapon = state.getMob().getItemInHand(hand);
        WeaponHandler handler = WeaponCombatUsage.WEAPON_HANDLERS.get(weapon.getItem());
        if (handler != null)
            handler.handle(state, weapon, hand);
    }
}
