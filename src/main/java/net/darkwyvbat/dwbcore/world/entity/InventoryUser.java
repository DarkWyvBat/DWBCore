package net.darkwyvbat.dwbcore.world.entity;

import net.darkwyvbat.dwbcore.world.entity.inventory.InventoryManager;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.npc.InventoryCarrier;

public interface InventoryUser extends InventoryCarrier, ContainerListener {

    InventoryManager<?> provideInventoryManager();

    InventoryManager<?> getInventoryManager();

    int getInventorySize();

    void cleanInventory(int slotsCount);
}
