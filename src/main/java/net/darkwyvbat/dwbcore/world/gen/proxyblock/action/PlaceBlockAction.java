package net.darkwyvbat.dwbcore.world.gen.proxyblock.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkwyvbat.dwbcore.world.block.ProxyBlock;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionType;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.Optional;

public record PlaceBlockAction(BlockState blockState, Optional<CompoundTag> nbt,
                               boolean copyFacing) implements ProxyBlockAction {

    public static final MapCodec<PlaceBlockAction> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                    BlockState.CODEC.fieldOf("block_state").forGetter(PlaceBlockAction::blockState),
                    TagParser.FLATTENED_CODEC.optionalFieldOf("nbt").forGetter(PlaceBlockAction::nbt),
                    Codec.BOOL.optionalFieldOf("copy_facing", false).forGetter(PlaceBlockAction::copyFacing)
            ).apply(i, PlaceBlockAction::new)
    );

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ServerLevel level, BlockPos pos, int depth) {
        if (depth > MAX_DEPTH) return;

        BlockState state = level.getBlockState(pos);
        BlockState finalState = this.blockState;
        if (this.copyFacing && state.getBlock() instanceof ProxyBlock) {
            Direction sourceDirection = state.getValue(ProxyBlock.FACING_PROPERTY);
            Property<?> property = finalState.getBlock().getStateDefinition().getProperty("facing");
            if (property != null && property.getPossibleValues().contains(sourceDirection))
                finalState = finalState.setValue((Property<Direction>) property, sourceDirection);
        }
        level.setBlock(pos, finalState, 3);
        this.nbt.ifPresent(t -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                CompoundTag nbtToLoad = t.copy();
                nbtToLoad.putInt("x", pos.getX());
                nbtToLoad.putInt("y", pos.getY());
                nbtToLoad.putInt("z", pos.getZ());
                blockEntity.loadWithComponents(TagValueInput.create(null, level.registryAccess(), nbtToLoad));
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public ProxyBlockActionType<? extends ProxyBlockAction> getType() {
        return ProxyBlockActionTypes.PLACE_BLOCK;
    }
}