package net.petercashel.dingusprimeacm.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class DingusPrimeWorldSaveData extends SavedData {

    int version = 0;

    public DingusPrimeWorldSaveData() {




    }

    public void load(CompoundTag nbt) {
        version = nbt.getInt("version");
        if (version == 0) {

        }



    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        //V0
        nbt.putInt("version", version);




        return nbt;
    }

    public void markDirty() {
        this.setDirty();
    }

}
