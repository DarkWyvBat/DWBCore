package net.darkwyvbat.dwbcore.world.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockUtils {
    public static BlockState copyProperties(BlockState from, BlockState to) {
        BlockState blockState = to;
        for (Property<?> property : from.getProperties())
            if (to.hasProperty(property)) blockState = copyProperty(from, blockState, property);
        return blockState;
    }

    static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}