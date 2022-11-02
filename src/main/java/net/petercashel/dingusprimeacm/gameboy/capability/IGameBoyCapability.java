package net.petercashel.dingusprimeacm.gameboy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGameBoyCapability extends INBTSerializable<CompoundTag> {
    String getUniqueID();
    void setUniqueID(String value);
}

