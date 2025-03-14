package net.petercashel.dingusprimeacm.world.daily;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.world.WorldDataManager;

import java.time.Instant;
import java.util.TimeZone;

public class DailyRewardData implements INBTSerializable<CompoundTag> {
    static int version = 1;
    static long day = 86400000;

    public boolean firstTimeGiven = false;
    public long nextTimestamp = 0;
    public int currentRewardLevel = 0;

    public enum DailyRewardStatus {
        FirstTime,
        TooEarly,
        NextReward,
        TooLate,
        NoReward
    }

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("version", version);
        nbt.putInt("currentRewardLevel", currentRewardLevel);
        nbt.putLong("nextTimestamp", nextTimestamp);
        nbt.putBoolean("firstTimeGiven", firstTimeGiven);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int version = nbt.getInt("version");
        currentRewardLevel = nbt.getInt("currentRewardLevel");
        nextTimestamp = nbt.getLong("nextTimestamp");
        firstTimeGiven = nbt.getBoolean("firstTimeGiven");
    }

    public long getTimeInMilli() {
        return System.currentTimeMillis();
    }

    public boolean hasReward() {
        if (!firstTimeGiven || nextTimestamp == 0) {
            return true;
        }
        if (nextTimestamp < getTimeInMilli()) return true;
        return false;
    }

    public int GetRewardLevel() {
        currentRewardLevel++;
        MarkDirty();
        return currentRewardLevel;
    }

    public DailyRewardStatus GetCurrentReward() {
        if (!firstTimeGiven) {
            firstTimeGiven = true;
            nextTimestamp = getTimeInMilli() + day;
            MarkDirty();
            return DailyRewardStatus.FirstTime;
        }

        if (nextTimestamp > getTimeInMilli())
        {
            return DailyRewardStatus.TooEarly;
        }
        if (nextTimestamp < getTimeInMilli() && nextTimestamp + day > getTimeInMilli())
        {
            if (DailyManager.GetMaxDailyRewardLevel() >= currentRewardLevel) {
                return DailyRewardStatus.NoReward;
            }
            nextTimestamp = getTimeInMilli() + day;
            MarkDirty();
            return DailyRewardStatus.NextReward;
        }
        if (nextTimestamp + day < getTimeInMilli())
        {
            return DailyRewardStatus.TooLate;
        }
        return DailyRewardStatus.NoReward;
    }
}
