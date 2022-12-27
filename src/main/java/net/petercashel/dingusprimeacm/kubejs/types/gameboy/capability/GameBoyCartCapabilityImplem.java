package net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.dingusprimeacm;

import java.util.UUID;

public class GameBoyCartCapabilityImplem implements IGameBoyCartCapability {
    private String value;

    public static final ResourceLocation ID = new ResourceLocation(dingusprimeacm.MODID, "gameboycartcap");
    private static final String NBT_KEY = "gameboycartcap";

    public GameBoyCartCapabilityImplem() { this(UUID.randomUUID().toString()); }
    public GameBoyCartCapabilityImplem(String uuid) { this.value = uuid; }

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
        tag.putString("uuidcart", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        value = nbt.getString("uuidcart");
        if (value.length() < 2) {
            //FUCK, Unbreak this
            value = UUID.randomUUID().toString();
        }
    }
}
