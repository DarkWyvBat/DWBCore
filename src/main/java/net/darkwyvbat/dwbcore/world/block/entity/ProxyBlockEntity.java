package net.darkwyvbat.dwbcore.world.block.entity;

import net.darkwyvbat.dwbcore.world.block.ProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class ProxyBlockEntity extends BlockEntity {

    private String pool = "";

    public ProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DwbBlockEntityType.PROXY_BLOCK, blockPos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        pool = valueInput.getStringOr(ProxyBlock.POOL_TAG, "");
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putString(ProxyBlock.POOL_TAG, pool);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    public String getPool() {
        return pool;
    }
}
