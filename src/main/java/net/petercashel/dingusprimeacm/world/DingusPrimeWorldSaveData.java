package net.petercashel.dingusprimeacm.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.petercashel.dingusprimeacm.world.zones.ZoneManager;

public class DingusPrimeWorldSaveData extends SavedData {

    static int version = 1;

    public DingusPrimeWorldSaveData() {

    }

    private void InitStatics() {
        ZoneManager.Instance = new ZoneManager();
    }

    public void load(CompoundTag nbt) {
        InitStatics();

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
