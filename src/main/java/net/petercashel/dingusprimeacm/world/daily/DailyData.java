package net.petercashel.dingusprimeacm.world.daily;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.world.WorldDataManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class DailyData implements INBTSerializable<CompoundTag> {
    static int version = 1;


    public ConcurrentHashMap<UUID, DailyRewardData> PlayerDailyRewardData = new ConcurrentHashMap<>();
    public ConcurrentSkipListMap<Integer, Integer> DailyRewardMap = new ConcurrentSkipListMap<>();



    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("version", version);

        CompoundTag PlayerDailyRewardData = SavePlayerDailyRewardData(new CompoundTag());
        nbt.put("PlayerDailyRewardData", PlayerDailyRewardData);


        return nbt;
    }

    private CompoundTag SavePlayerDailyRewardData(CompoundTag tag) {
        tag.putInt("count", PlayerDailyRewardData.size());
        int i = 0;
        for (var entry : PlayerDailyRewardData.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("UUID", entry.getKey());
            nbt.put("data", entry.getValue().serializeNBT());
            tag.put(Integer.toString((i++)), nbt);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int version = nbt.getInt("version");
        PlayerDailyRewardData.clear();

        CompoundTag PlayerDailyRewardData = nbt.getCompound("PlayerDailyRewardData");
        LoadPlayerDailyRewardData(PlayerDailyRewardData);



    }

    private void LoadPlayerDailyRewardData(CompoundTag tag) {
        int count = tag.getInt("count");
        for (int i = 0; i < count; i++) {
            CompoundTag nbt = tag.getCompound(Integer.toString(i));
            DailyRewardData data = new DailyRewardData();
            UUID uuid = nbt.getUUID("UUID");
            data.deserializeNBT(nbt.getCompound("data"));

            PlayerDailyRewardData.put(uuid, data);
        }
    }

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }

    public DailyRewardData GetPlayerDailyRewardData(Player player)  {
        DailyRewardData rewardData = null;

        if (!PlayerDailyRewardData.containsKey(player.getUUID())) {
            PlayerDailyRewardData.put(player.getUUID(), new DailyRewardData());
            MarkDirty();
        }

        rewardData = PlayerDailyRewardData.get(player.getUUID());

        return rewardData;
    }
}
