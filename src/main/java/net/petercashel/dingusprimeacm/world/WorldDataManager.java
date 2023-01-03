package net.petercashel.dingusprimeacm.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.petercashel.dingusprimeacm.world.zones.ZoneManager;

public class WorldDataManager {

    public static DingusPrimeWorldSaveData SaveDataInstance = null;

    public static void OnServerStarting(ServerStartingEvent event) {
        DimensionDataStorage dataStorage = event.getServer().overworld().getDataStorage();

        SaveDataInstance = dataStorage.computeIfAbsent(WorldDataManager::LoadWorldSaveData, WorldDataManager::CreateWorldSaveData, "dingusprimeacmdata");
    }

    public static DingusPrimeWorldSaveData CreateWorldSaveData() {
        return new DingusPrimeWorldSaveData();
    }

    public static DingusPrimeWorldSaveData LoadWorldSaveData(CompoundTag tag) {
        DingusPrimeWorldSaveData data = CreateWorldSaveData();
        // Load saved data
        if (tag != null && !tag.isEmpty()) {
            data.load(tag);
        }



        return data;
    }

    public static void OnServerStarted(ServerStartedEvent event) {
        ServerLevel Overworld = event.getServer().overworld();
        ZoneManager.Instance.Data.isServerInstance = true;

    }
}
