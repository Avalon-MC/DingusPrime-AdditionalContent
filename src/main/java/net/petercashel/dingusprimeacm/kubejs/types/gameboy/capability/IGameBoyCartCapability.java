package net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.common.util.INBTSerializable;

public interface IGameBoyCartCapability extends INBTSerializable<CompoundTag> {
    String getUniqueID();
    void setUniqueID(String value);
}

