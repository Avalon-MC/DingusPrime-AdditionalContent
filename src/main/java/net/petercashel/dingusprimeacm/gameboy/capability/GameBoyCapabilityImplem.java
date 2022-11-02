package net.petercashel.dingusprimeacm.gameboy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.dingusprimeacm;

import java.util.UUID;

public class GameBoyCapabilityImplem implements IGameBoyCapability {
    private String value;

    public static final ResourceLocation ID = new ResourceLocation(dingusprimeacm.MODID, "gameboycap");
    private static final String NBT_KEY = "gameboycap";

    public GameBoyCapabilityImplem() { this(UUID.randomUUID().toString()); }
    public GameBoyCapabilityImplem(String uuid) { this.value = uuid; }

    @Override
    public String getUniqueID() {
        return this.value;
    }

    @Override
    public void setUniqueID(String value) {
        this.value = value;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("uuid", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        value = nbt.getString("uuid");
    }
}
