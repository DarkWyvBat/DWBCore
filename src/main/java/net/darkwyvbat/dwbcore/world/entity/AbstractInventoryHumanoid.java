package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.world.entity.inventory.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractInventoryHumanoid extends AbstractHumanoidEntity implements InventoryUser {

    protected static final Vec3 ITEM_PICKUP_REACH = new Vec3(1.2, 0.7, 1.2);

    protected final HumanoidInventoryManager inventoryManager;
    public boolean shouldRevisionItems = true;
    protected List<ItemEntity> wantedItems = new ArrayList<>();

    protected AbstractInventoryHumanoid(EntityType<? extends AbstractInventoryHumanoid> entityType, Level level) {
        super(entityType, level);
        inventoryManager = provideInventoryManager();
        inventoryManager.getInventory().addListener(this);
        inventoryManager.updateInventoryEntries();
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (tickCount % 64 == 0)
            wantedItems = inspectItemEntities(perception.getLastScan().itemsAround());

        for (ItemEntity item : this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(ITEM_PICKUP_REACH.x, ITEM_PICKUP_REACH.y, ITEM_PICKUP_REACH.z)))
            if (!item.isRemoved() && !item.getItem().isEmpty() && !item.hasPickUpDelay() && this.wantsToPickUp(serverLevel, item.getItem()))
                this.pickUpItem(serverLevel, item);

        useItemCD.tick();
    }

    @Override
    public HumanoidInventoryManager provideInventoryManager() {
        return new HumanoidInventoryManager(new SimpleContainer(getInventorySize()), new DefaultInventoryItemCategorizer(), HumanoidInventoryManager.COMPARATORS, HumanoidInventoryManager.ITEMS_IMPORTANCE_ORDER);
    }

    @Override
    public HumanoidInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public int getInventorySize() {
        return 16;
    }

    @Override
    public @NotNull SimpleContainer getInventory() {
        return this.inventoryManager.getInventory();
    }

    @Override
    public void containerChanged(Container container) {
        inventoryManager.updateInventoryEntries();
        shouldRevisionItems = true;
    }

    @Override
    public void onEquippedItemBroken(Item item, EquipmentSlot equipmentSlot) {
        super.onEquippedItemBroken(item, equipmentSlot);
        containerChanged(getInventory());
    }

    @Override
    protected void pickUpItem(ServerLevel serverLevel, ItemEntity itemEntity) {
        this.onItemPickup(itemEntity);
        InventoryCarrier.pickUpItem(serverLevel, this, this, itemEntity);
        inventoryManager.updateInventoryEntries();
    }

    @Override
    public boolean wantsToPickUp(ServerLevel serverLevel, ItemStack itemStack) {
        return serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.isAlive();
    }

    public List<ItemEntity> inspectItemEntities(List<ItemEntity> entities) {
        List<ItemEntity> wantedItems = new ArrayList<>();
        for (ItemEntity itemEntity : entities) {
            ItemStack worldItem = itemEntity.getItem();
            if (worldItem.isEmpty()) continue;
            for (InventoryItemCategory category : inventoryManager.getCategorizer().categorize(worldItem)) {
                Comparator<ItemStack> comparator = inventoryManager.getItemComparators().get(category);
                switch (category) {
                    case ARMOR -> {
                        EquipmentSlot worldItemSlot = worldItem.get(DataComponents.EQUIPPABLE).slot();
                        ItemStack invArmor = getInventoryManager().getBestArmorForSlot(worldItemSlot);
                        if (comparator.compare(worldItem, invArmor) < 0) wantedItems.add(itemEntity);
                    }
                    case RANGED_WEAPON -> {
                        if (inventoryManager.getInventoryEntry(InventoryItemCategory.RANGED_WEAPON).isEmpty())
                            wantedItems.add(itemEntity);
                    }
                    case MELEE_WEAPON -> {
                        if (comparator.compare(worldItem, inventoryManager.getFirstItemInEntry(category)) < 0)
                            wantedItems.add(itemEntity);
                    }
                    case SHIELD_OR_SUPPORT -> {
                        if (inventoryManager.getInventoryEntry(InventoryItemCategory.SHIELD_OR_SUPPORT).isEmpty())
                            wantedItems.add(itemEntity);
                    }
                    case CONSUMABLE -> {
                        if (!isEnoughForCare() || comparator.compare(worldItem, inventoryManager.getFirstItemInEntry(category)) < 0)
                            wantedItems.add(itemEntity);
                    }
                }
            }
        }
        return wantedItems;
    }

    @Override
    public void cleanInventory(int slotsToFree) {
        CategoryCollector<InventoryItemCategory> armorStrategy = (items, count, trash, inv, category) -> {
            Set<EquipmentSlot> set = EnumSet.noneOf(EquipmentSlot.class);
            int total = 0;
            for (int i : items) {
                if (total >= count) return;
                EquipmentSlot slot = getInventory().getItem(i).get(DataComponents.EQUIPPABLE).slot();
                if (!set.contains(slot)) set.add(slot);
                else {
                    trash.add(i);
                    ++total;
                }
            }
        };
        Map<InventoryItemCategory, CategoryCollector<InventoryItemCategory>> strategies = new EnumMap<>(InventoryItemCategory.class);
        strategies.put(InventoryItemCategory.OTHER, (items, count, trash, inv, category) -> {
            for (int i = 0; i < count; i++) {
                if (items.size() - 1 - i < 0) break;
                trash.add(items.get(items.size() - 1 - i));
            }
        });
        strategies.put(InventoryItemCategory.ARMOR, armorStrategy);
        Set<Integer> trashIndices = inventoryManager.getPotentialTrash(slotsToFree, strategies);
        throwItems(trashIndices, null, 1.0F, 40);
        trashIndices.forEach(i -> inventoryManager.getInventory().removeItem(i, inventoryManager.getInventory().getItem(i).getCount()));
    }

    public void throwItem(int i, int c, Vec3 dest, float f, int delay, boolean shouldUpdate) {
        swing(InteractionHand.MAIN_HAND);
        Vec3 dir = dest == null ? Vec3.directionFromRotation(getRotationVector()).scale(f) : dest.normalize().scale(f);
        spawnThrownItem(inventoryManager.getInventory().getItem(i), dir, f, delay);
        if (shouldUpdate) inventoryManager.getInventory().removeItem(i, c);
        else inventoryManager.getInventory().removeItemNoUpdate(i);
    }

    public void throwItems(Set<Integer> items, Vec3 dest, float f, int delay) {
        swing(InteractionHand.MAIN_HAND);
        for (Integer i : items)
            throwItem(i, inventoryManager.getInventory().getItem(i).getCount(), dest, 0.3F, delay, false);

        inventoryManager.getInventory().setChanged();
    }

    public void setUpArmor() {
        Set<EquipmentSlot> equippedInThisTick = new HashSet<>();

        List<Integer> armorIndices = new ArrayList<>(inventoryManager.getInventoryEntry(InventoryItemCategory.ARMOR));

        for (int armorIndex : armorIndices) {
            ItemStack item = getInventory().getItem(armorIndex);
            if (item.isEmpty()) continue;

            EquipmentSlot slot = item.get(DataComponents.EQUIPPABLE).slot();
            if (!equippedInThisTick.contains(slot)) {
                equipFromInventory(slot, armorIndex);
                equippedInThisTick.add(slot);
            }
        }
    }

    protected void completeUsingItem() {
        InteractionHand hand = getUsedItemHand();
        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        Integer invIndex = inventoryManager.getEquipmentSlotsInvRefs().get(slot);
        super.completeUsingItem();
        if (invIndex != null)
            getInventory().setItem(invIndex, getItemInHand(hand).copy());

        inventoryManager.updateInventoryEntries();
    }

    public int equipFromInventory(EquipmentSlot targetSlot, int newInvIndex) {
        Integer oldInvIndex = inventoryManager.getEquipmentSlotsInvRefs().get(targetSlot);
        if (oldInvIndex.equals(newInvIndex)) return oldInvIndex;
        ItemStack item = newInvIndex != InventoryManager.INVALID_INDEX ? getInventory().getItem(newInvIndex) : ItemStack.EMPTY;
        setItemSlot(targetSlot, item);
        inventoryManager.getEquipmentSlotsInvRefs().put(targetSlot, newInvIndex);
        return newInvIndex;
    }

    @Override
    public void disarm() {
        for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR)
            equipFromInventory(equipmentSlot, InventoryManager.INVALID_INDEX);
    }

    @Override
    public void freeHands() {
        equipFromInventory(EquipmentSlot.MAINHAND, InventoryManager.INVALID_INDEX);
        equipFromInventory(EquipmentSlot.OFFHAND, InventoryManager.INVALID_INDEX);
    }

    public boolean isEnoughForCare() {
        int c = 0;
        List<Integer> items = inventoryManager.getInventoryEntry(InventoryItemCategory.CONSUMABLE);
        for (int item : items)
            c += getInventory().getItem(item).getCount();
        return c > 10;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        this.writeInventoryToTag(valueOutput);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.readInventoryFromTag(valueInput);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean bl) {
        this.inventoryManager.getInventory().removeAllItems().forEach(itemStack -> {
            if (!EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP))
                this.spawnAtLocation(serverLevel, itemStack);
        });
    }

    public List<ItemEntity> getWantedItems() {
        return this.wantedItems;
    }

}