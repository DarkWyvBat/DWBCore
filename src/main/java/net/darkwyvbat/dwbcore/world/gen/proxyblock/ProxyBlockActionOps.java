package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;
import static net.darkwyvbat.dwbcore.DwbCore.LOGGER;

public class ProxyBlockActionOps {
    private static final Map<Identifier, Consumer<?>> OPS = new HashMap<>();

    public static final ProxyBlockActionOp<Mob> MOB_BABY = register(INFO.id("baby"), e -> e.setBaby(true));

    public static void init() {
    }

    public static ProxyBlockPoolBuilder chest() {
        return new ProxyBlockPoolBuilder().block(Blocks.CHEST, true);
    }

    public static ProxyBlockPoolBuilder trappedChest() {
        return new ProxyBlockPoolBuilder().block(Blocks.TRAPPED_CHEST, true);
    }

    public static ProxyBlockPoolBuilder dispenser() {
        return new ProxyBlockPoolBuilder().block(Blocks.DISPENSER, true);
    }

    public static ProxyBlockPoolBuilder dropper() {
        return new ProxyBlockPoolBuilder().block(Blocks.DROPPER, true);
    }

    public static ProxyBlockPoolBuilder barrel() {
        return new ProxyBlockPoolBuilder().block(Blocks.BARREL, true);
    }

    public static ProxyBlockPoolBuilder decoratedPot() {
        return new ProxyBlockPoolBuilder().block(Blocks.DECORATED_POT);
    }

    public static ProxyBlockPoolBuilder furnace() {
        return new ProxyBlockPoolBuilder().block(Blocks.FURNACE, true);
    }

    public static ProxyBlockPoolBuilder blastFurnace() {
        return new ProxyBlockPoolBuilder().block(Blocks.BLAST_FURNACE, true);
    }

    public static ProxyBlockPoolBuilder smoker() {
        return new ProxyBlockPoolBuilder().block(Blocks.SMOKER, true);
    }

    public static ProxyBlockPoolBuilder craftingTable() {
        return new ProxyBlockPoolBuilder().block(Blocks.CRAFTING_TABLE);
    }

    public static <T> ProxyBlockActionOp<T> register(Identifier id, Consumer<T> consumer) {
        OPS.put(id, consumer);
        return new ProxyBlockActionOp<>(id, consumer);
    }

    @SuppressWarnings("unchecked")
    public static <T> void run(Identifier id, T context) {
        Consumer<?> consumer = OPS.get(id);
        if (consumer != null) {
            try {
                ((Consumer<T>) consumer).accept(context);
            } catch (Exception e) {
                LOGGER.error("ProxyBlock op failed: {} {}", id, e);
            }
        }
    }

    private static void applyLootTable(BlockInWorld blockInWorld, ResourceKey<LootTable> lootTable) {
        if (blockInWorld.getEntity() instanceof RandomizableContainer container) {
            if (blockInWorld.getLevel() instanceof ServerLevel serverLevel)
                container.setLootTable(lootTable, serverLevel.getRandom().nextLong());
        }
    }

    public static ProxyBlockActionOp<BlockInWorld> createLootTable(Identifier id, ResourceKey<LootTable> lootTable) {
        return register(id.withPath("loot_table/" + id.getPath()), b -> applyLootTable(b, lootTable));
    }
}