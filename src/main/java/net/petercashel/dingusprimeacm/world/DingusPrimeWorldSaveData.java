package net.petercashel.dingusprimeacm.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.petercashel.dingusprimeacm.world.daily.DailyManager;
import net.petercashel.dingusprimeacm.world.zones.ZoneManager;

public class DingusPrimeWorldSaveData extends SavedData {

    static int version = 2;

    public DingusPrimeWorldSaveData() {

    }

    private void InitStatics() {

        DailyManager.Instance = new DailyManager();//System.currentTimeMillis()
    }

    public void load(CompoundTag nbt) {
        InitStatics();

        int version = nbt.getInt("version");
        if (version == 0) {

        }
        if (version == 1) {
            //WasZoneManager
        }

        if (version == 2) {
            DailyManager.Instance.Data.deserializeNBT(nbt.getCompound("DailyManager"));
            DailyManager.LoadDailyRewards();
        }


    }


    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        //V0
        nbt.putInt("version", version);

        //V1
        //WasZoneManager

        //V2
        nbt.put("DailyManager", DailyManager.Instance.Data.serializeNBT());

        return nbt;
    }

    public void markDirty() {
        this.setDirty();
    }

}
