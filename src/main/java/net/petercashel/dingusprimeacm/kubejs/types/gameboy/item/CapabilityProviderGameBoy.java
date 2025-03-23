package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class CapabilityProviderGameBoy implements INBTSerializable<CompoundTag>, ICapabilityProvider<GameBoyCartItemJS, Void> {

    ItemStackHandler backend = new ItemStackHandler(1);
    Lazy<IItemHandler> optionalStorage = Lazy.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("cart", backend.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt != null) {
            CompoundTag tag = nbt.getCompound("cart");

            if (tag.isEmpty() && nbt.contains("Parent")) {
                tag = nbt.getCompound("Parent");
                tag = tag.getCompound("cart");
            }

            if (!tag.isEmpty()) {
                backend.deserializeNBT(provider,tag);
            }
        }
    }



    @Override
    public @Nullable Object getCapability(Object object, Object context) {
        return null;
    }
}
