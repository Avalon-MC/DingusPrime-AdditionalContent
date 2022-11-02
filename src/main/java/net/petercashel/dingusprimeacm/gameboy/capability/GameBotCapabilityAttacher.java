package net.petercashel.dingusprimeacm.gameboy.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.kubejs.GameBoyItemJS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameBotCapabilityAttacher {
    private static class GameBotCapabilityProvider implements  ICapabilityProvider, ICapabilitySerializable<CompoundTag>, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(dingusprimeacm.MODID, "gameboycap");

        private final IGameBoyCapability backend = new GameBoyCapabilityImplem();
        private final LazyOptional<IGameBoyCapability> optionalData = LazyOptional.of(() -> backend);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return dingusprimeacm.GAMEBOY_CAP_INSTANCE.orEmpty(cap, this.optionalData);
        }

        void invalidate() {
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }


    public static void attach(final AttachCapabilitiesEvent<ItemStack> event) {
        final GameBotCapabilityProvider provider = new GameBotCapabilityProvider();
        if (event.getObject().getItem() instanceof GameBoyItemJS) {
            event.addCapability(GameBotCapabilityProvider.IDENTIFIER, provider);
        }
    }

    private GameBotCapabilityAttacher() {
    }
}
