package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.tag.DwbItemTags;
import net.darkwyvbat.dwbcore.world.entity.ai.WeaponActions;
import net.darkwyvbat.dwbcore.world.entity.inventory.InventoryItemCategory;
import net.darkwyvbat.dwbcore.world.entity.inventory.InventoryManager;
import net.darkwyvbat.dwbcore.world.entity.specs.OmniWarrior;
import net.darkwyvbat.dwbcore.world.entity.specs.PotionAttacker;
import net.darkwyvbat.dwbcore.world.entity.specs.SelfCaring;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;

public abstract class CombatantInventoryHumanoid extends AbstractInventoryHumanoid implements OmniWarrior, PotionAttacker, SelfCaring {
    protected CombatantInventoryHumanoid(EntityType<? extends CombatantInventoryHumanoid> entityType, Level level) {
        super(entityType, level);
    }

    public void prepareForFight() {
        prepareMelee();
        setUpArmor();
        prepareForAttackBlocking();
    }

    @Override
    public boolean hasAttackPotions() {
        return inventoryManager.entryNotEmpty(InventoryItemCategory.ATTACK_POTION);
    }

    @Override
    public Set<Holder<MobEffect>> getAvailableAttackEffects() {
        return inventoryManager.getAvailablePotionEffectsWithIndices().keySet();
    }

    @Override
    public void preparePotionAttack(Holder<MobEffect> effect, EquipmentSlot slot) {
        equipFromInventory(slot, inventoryManager.getPotionWithEffectIndex(effect));
    }

    @Override
    public boolean hasForCare() {
        return inventoryManager.getForHealIndex() != InventoryManager.INVALID_INDEX;
    }

    @Override
    public void prepareForCare(InteractionHand hand) {
        equipFromInventory(hand.asEquipmentSlot(), inventoryManager.getForHealIndex());
    }

    @Override
    public void startCaring(InteractionHand hand) {
        startUsingItem(hand);
    }

    @Override
    public void stopCaring() {
        stopUsingItem();
    }

    @Override
    public float getHealthPercent() {
        return getHealth() / getMaxHealth();
    }

    @Override
    public boolean isFullHealth() {
        return getHealth() >= getMaxHealth();
    }

    @Override
    public boolean hasMelee() {
        return inventoryManager.entryNotEmpty(InventoryItemCategory.MELEE_WEAPON);
    }

    @Override
    public boolean readyForMelee() {
        return getItemInHand(InteractionHand.MAIN_HAND).is(DwbItemTags.MELEE_WEAPONS);
    }

    @Override
    public void prepareMelee() {
        equipFromInventory(EquipmentSlot.MAINHAND, inventoryManager.getFirstIndexInEntry(InventoryItemCategory.MELEE_WEAPON));
    }

    @Override
    public boolean hasRanged() {
        return inventoryManager.entryNotEmpty(InventoryItemCategory.RANGED_WEAPON);
    }

    @Override
    public boolean readyForRanged() {
        return getItemInHand(InteractionHand.MAIN_HAND).is(DwbItemTags.RANGED_WEAPONS);
    }

    @Override
    public void prepareRanged() {
        equipFromInventory(EquipmentSlot.MAINHAND, inventoryManager.getFirstIndexInEntry(InventoryItemCategory.RANGED_WEAPON));
    }

    @Override
    public void performRangedAttack(Entity target, InteractionHand hand, float charge) {
        ItemStack item = getItemInHand(hand);
        WeaponActions.get(item).ifPresent(a -> a.use(this, item, target, charge));
    }

    @Override
    public boolean hasAttackBlocker() {
        return inventoryManager.entryNotEmpty(InventoryItemCategory.SHIELD_OR_SUPPORT);
    }

    @Override
    public boolean readyForBlockAttack() {
        return useItemCD.isReady() && getItemInHand(InteractionHand.OFF_HAND).has(DataComponents.BLOCKS_ATTACKS);
    }

    @Override
    public void prepareForAttackBlocking() {
        equipFromInventory(EquipmentSlot.OFFHAND, inventoryManager.getFirstIndexInEntry(InventoryItemCategory.SHIELD_OR_SUPPORT));
    }

    @Override
    public void startBlockAttack() {
        if (readyForBlockAttack())
            startUsingItem(InteractionHand.OFF_HAND);
    }

    @Override
    public void stopBlockAttack() {
        stopUsingItem();
    }

}
