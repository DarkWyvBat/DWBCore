package net.darkwyvbat.dwbcore.world.block;

import com.mojang.serialization.MapCodec;
import net.darkwyvbat.dwbcore.world.block.entity.ProxyBlockEntity;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProxyBlock extends BaseEntityBlock {
    public static final MapCodec<ProxyBlock> CODEC = simpleCodec(ProxyBlock::new);

    public static final BooleanProperty EXECUTE_PROPERTY = BooleanProperty.create("execute");
    public static final EnumProperty<Direction> FACING_PROPERTY = BlockStateProperties.FACING;
    public static final EnumProperty<Appearance> APPEARANCE_PROPERTY = EnumProperty.create("appearance", Appearance.class);

    public static final String POOL_TAG = "pool";
    public static final Map<String, ProxyBlockPool> POOLS = new HashMap<>();

    @Override
    public @NotNull MapCodec<ProxyBlock> codec() {
        return CODEC;
    }

    public ProxyBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EXECUTE_PROPERTY, false).setValue(FACING_PROPERTY, Direction.NORTH).setValue(APPEARANCE_PROPERTY, Appearance.GENERIC));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState prevState, boolean isMoving) {
        super.onPlace(state, level, pos, prevState, isMoving);

        if (state.getValue(EXECUTE_PROPERTY))
            level.scheduleTick(pos, this, 4);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(EXECUTE_PROPERTY))
            executeSpawnLogic(level, pos, random);
    }

    private void executeSpawnLogic(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ProxyBlockEntity proxyBlockEntity) {
            ProxyBlockPool pool = POOLS.get(proxyBlockEntity.getPool());
            if (pool != null)
                pool.getRandomAction(random).ifPresentOrElse(a -> a.execute(level, pos, 0), () -> level.setBlock(pos, pool.fallback(), 3));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING_PROPERTY, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING_PROPERTY, rotation.rotate(blockState.getValue(FACING_PROPERTY)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING_PROPERTY)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EXECUTE_PROPERTY, FACING_PROPERTY, APPEARANCE_PROPERTY);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProxyBlockEntity(blockPos, blockState);
    }

    public enum Appearance implements StringRepresentable {
        GENERIC("generic"),
        SELECTOR("selector"),
        FEATURE("feature"),
        BLOCK("block"),
        CHEST("chest"),
        DISPENSER("dispenser"),
        MOB("mob"),
        MONSTER("monster");

        private final String name;

        Appearance(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
