package net.petercashel.dingusprimeacm.kubejs.types.cabnet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;





import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.block.CartShelfBlockJS;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CabnetBlockEntity extends BlockEntity {
    public Direction FACING;

    ItemStackHandler backend = new ItemStackHandler(16);
    LazyOptional<IItemHandler> optionalStorage = LazyOptional.of(() -> backend);

    public CabnetBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        this.FACING = pBlockState.getValue(CartShelfBlockJS.FACING);
    }

    public CabnetBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(dingusprimeKubeJSPlugin.CABNET_BE, blockPos, blockState);
        this.FACING = blockState.getValue(CartShelfBlockJS.FACING);
    }

    //Cap

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optionalStorage.cast();
        }
        return super.getCapability(cap);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optionalStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    //Saving

    public void MarkDirtySaveData() {
        if (!this.level.isClientSide()) {
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        serializeCapNBT(pTag);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        deserializeCapNBT(pTag);

    }

    public CompoundTag serializeCapNBT(CompoundTag tag) {
        tag.put("storage", backend.serializeNBT());
        return tag;
    }

    public void deserializeCapNBT(CompoundTag nbt) {
        if (nbt != null) {
            CompoundTag tag = nbt.getCompound("storage");

            if (tag.isEmpty() && nbt.contains("Parent")) {
                tag = tag.getCompound("Parent");
                tag = tag.getCompound("storage");
            }

            if (!tag.isEmpty()) {
                backend.deserializeNBT(tag);
            }
        }
    }

    //Network Sync

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        serializeCapNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        deserializeCapNBT(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStackHandler getItemStackHandler() {
        return backend;
    }
}
