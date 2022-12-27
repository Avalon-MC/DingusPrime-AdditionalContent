package net.petercashel.dingusprimeacm.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.world.Zones.ZoneManager;

public class DingusPrimeWorldSaveData extends SavedData {

    static int version = 1;

    public DingusPrimeWorldSaveData() {




    }

    public void load(CompoundTag nbt) {
        int version = nbt.getInt("version");
        if (version == 0) {

        }
        if (version == 1) {
            ZoneManager.Instance.Data.Load(nbt.getCompound("ZoneManager"));
        }



    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        //V0
        nbt.putInt("version", version);

        //V1
        nbt.put("ZoneManager", ZoneManager.Instance.Data.Save(new CompoundTag()));

        return nbt;
    }

    public void markDirty() {
        this.setDirty();
    }

}
