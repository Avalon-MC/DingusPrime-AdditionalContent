package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.common.capabilities.Capability;
import net.neoforged.common.capabilities.ICapabilityProvider;
import net.neoforged.common.capabilities.ICapabilitySerializable;
import net.neoforged.common.util.INBTSerializable;
import net.neoforged.common.util.LazyOptional;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability.GameBoyCartCapabilityImplem;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability.IGameBoyCartCapability;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class GameBoyCartItemJS extends BasicItemJS {

    public String gameID;

    public GameBoyCartItemJS(GBCartridgeBuilder p) {
        super(p);
        gameID = p.gameID;
    }
    //defaultrom

    public static IGameBoyCartCapability GetGameboyCartCapFromStack(ItemStack stack) {
        if (stack.getItem() instanceof GameBoyCartItemJS)
        {
            LazyOptional<IGameBoyCartCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOYCART_CAP_INSTANCE);
            if (cap.isPresent()) {
                IGameBoyCartCapability capability = cap.resolve().get();
                return capability;
            }
        }
        return null;
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {

        CompoundTag result  = new CompoundTag();
        CompoundTag tag = super.getShareTag(stack);
        CompoundTag cartcap = GetGameboyCartCapFromStack(stack).serializeNBT();

        if (tag != null)
            result.put("tag", tag);
        if (cartcap != null)
            result.put("cartcap", cartcap);

        return result ;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {

        if (nbt == null) {
            stack.setTag(nbt);
        } else {
            stack.setTag(nbt.getCompound("tag"));
            GetGameboyCartCapFromStack(stack).deserializeNBT(nbt.getCompound("cartcap"));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        GameBoyCartCapabilityProvider newProvider = new GameBoyCartCapabilityProvider();
        if (nbt != null) {
            newProvider.deserializeNBT(nbt);
        }
        return newProvider;
    }


    public class GameBoyCartCapabilityProvider implements  ICapabilityProvider, ICapabilitySerializable<CompoundTag>, INBTSerializable<CompoundTag> {

        private final IGameBoyCartCapability backend = new GameBoyCartCapabilityImplem();
        private final LazyOptional<IGameBoyCartCapability> optionalData = LazyOptional.of(() -> backend);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
            return dingusprimeacm.GAMEBOYCART_CAP_INSTANCE.orEmpty(cap, this.optionalData);
        }

        void invalidate() {
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("cartdata", backend.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            if (nbt != null) {
                CompoundTag tag = nbt.getCompound("cartdata");

                if (tag.isEmpty() && nbt.contains("Parent")) {
                    tag = nbt.getCompound("Parent");
                    tag = tag.getCompound("cartdata");
                }

                if (tag.isEmpty() && nbt.contains("dingusprimeacm:gameboycartcap")) {
                    tag = nbt.getCompound("dingusprimeacm:gameboycartcap");
                }

                if (!tag.isEmpty()) {
                    backend.deserializeNBT(tag);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        LazyOptional<IGameBoyCartCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOYCART_CAP_INSTANCE);
        if (cap.isPresent()) {
            IGameBoyCartCapability capability = cap.resolve().get();
            if (capability.getUniqueID() == null || capability.getUniqueID().isBlank()) {
                capability.setUniqueID(UUID.randomUUID().toString());
            }
            tooltip.add(new TextComponent("UUID: " + capability.getUniqueID()));
        } else {
            tooltip.add(new TextComponent("UUID: CAP MISSING"));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }



    public static class GBCartridgeBuilder extends ItemBuilder {

        String gameID = "defaultrom";

        public GBCartridgeBuilder(ResourceLocation i) {
            //super(i, 6.0F, -3.1F);
            super(i);

            this.parentModel("minecraft:item/handheld");
            this.unstackable();
            this.texture("kubejs:item/gbcart"); //default
            gameID("defaultrom");
        }

        public GameBoyCartItemJS createObject() {
            return new GameBoyCartItemJS(this);
        }

        public GBCartridgeBuilder gameID(String v) {
            this.gameID = v;
            return this;
        }
    }

}
