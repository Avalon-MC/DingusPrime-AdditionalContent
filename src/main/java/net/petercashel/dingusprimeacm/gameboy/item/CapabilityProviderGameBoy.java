package net.petercashel.dingusprimeacm.gameboy.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CapabilityProviderGameBoy implements INBTSerializable<CompoundTag>,ICapabilityProvider {

    ItemStackHandler backend = new ItemStackHandler(1);
    LazyOptional<IItemHandler> optionalStorage = LazyOptional.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("cart", backend.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt != null) {
            CompoundTag tag = nbt.getCompound("cart");

            if (tag.isEmpty() && nbt.contains("Parent")) {
                tag = tag.getCompound("Parent");
                tag = tag.getCompound("cart");
            }

            if (!tag.isEmpty()) {
                backend.deserializeNBT(tag);
            }
        }
    }
}
