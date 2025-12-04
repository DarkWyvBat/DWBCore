package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.world.entity.ai.ItemInspector;
import net.darkwyvbat.dwbcore.world.entity.inventory.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractInventoryHumanoid extends AbstractHumanoidEntity implements InventoryUser {

    protected static final Vec3 ITEM_PICKUP_REACH = new Vec3(1.2, 0.7, 1.2);

    protected final HumanoidInventoryManager inventoryManager;
    public boolean shouldRevisionItems = true;
    protected List<ItemEntity> wantedItems = new ArrayList<>();

    protected AbstractInventoryHumanoid(EntityType<? extends AbstractInventoryHumanoid> entityType, Level level) {
        super(entityType, level);
        inventoryManager = createInventoryManager();
        inventoryManager.getInventory().addListener(this);
    }

    protected abstract MobInventoryProfile getInventoryProfile();

    protected void populateInventory() {
        inventoryManager.addItems(getInventoryProfile().items());
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        populateInventory();
        inventoryManager.updateInventoryEntries();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
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
    public HumanoidInventoryManager createInventoryManager() {
        return new HumanoidInventoryManager(new SimpleContainer(getInventorySize()), getInventoryProfile().inventoryConfig());
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
        onItemPickup(itemEntity);
        InventoryCarrier.pickUpItem(serverLevel, this, this, itemEntity);
        inventoryManager.updateInventoryEntries();
    }

    @Override
    public boolean wantsToPickUp(ServerLevel serverLevel, ItemStack itemStack) {
        return serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.isAlive();
    }

    public List<ItemEntity> inspectItemEntities(List<ItemEntity> entities) {
        Set<ItemEntity> wanted = new HashSet<>();
        for (ItemEntity itemEntity : entities) {
            ItemStack worldItem = itemEntity.getItem();
            if (worldItem.isEmpty()) continue;
            for (ItemCategory category : inventoryManager.getCategorizer().categorize(worldItem)) {
                ItemInspector inspector = getInventoryProfile().itemInspectors().get(category);
                if (inspector != null && inspector.isWanted(this, worldItem, category, inventoryManager)) {
                    wanted.add(itemEntity);
                    break;
                }
            }
        }
        return new ArrayList<>(wanted);
    }

    @Override
    public void cleanInventory(int slotsToFree) {
        Set<Integer> trashIndices = inventoryManager.getPotentialTrash(slotsToFree, getInventoryProfile().inventoryConfig().cleanStrategies());
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
        for (int i : items)
            throwItem(i, inventoryManager.getInventory().getItem(i).getCount(), dest, 0.3F, delay, false);

        inventoryManager.getInventory().setChanged();
    }

    public void prepareArmor() {
        Set<EquipmentSlot> alreadyEquipped = new HashSet<>();
        List<Integer> armorIndices = inventoryManager.getInventoryEntry(DwbItemCategories.ARMOR);
        for (int armorIndex : armorIndices) {
            ItemStack item = getInventory().getItem(armorIndex);
            if (item.isEmpty()) continue;
            EquipmentSlot slot = item.get(DataComponents.EQUIPPABLE).slot();
            if (!alreadyEquipped.contains(slot)) {
                equipFromInventory(slot, armorIndex);
                alreadyEquipped.add(slot);
            }
        }
    }

    protected void completeUsingItem() {
        InteractionHand hand = getUsedItemHand();
        EquipmentSlot slot = hand.asEquipmentSlot();
        int invIndex = inventoryManager.getEquipmentSlotsInvRefs().get(slot);
        super.completeUsingItem();
        if (invIndex != -1)
            getInventory().setItem(invIndex, getItemInHand(hand));

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

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        writeInventoryToTag(valueOutput);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        readInventoryFromTag(valueInput);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean bl) {
        inventoryManager.getInventory().removeAllItems().forEach(itemStack -> {
            if (!EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP))
                spawnAtLocation(serverLevel, itemStack);
        });
    }

    public List<ItemEntity> getWantedItems() {
        return wantedItems;
    }
}