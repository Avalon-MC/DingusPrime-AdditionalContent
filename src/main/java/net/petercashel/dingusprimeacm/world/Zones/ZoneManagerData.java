package net.petercashel.dingusprimeacm.world.Zones;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import net.petercashel.dingusprimeacm.world.Zones.Types.AntiBuildZone;
import net.petercashel.dingusprimeacm.world.Zones.Types.OwnerZone;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneManagerData {
    static int version = 1;
    public ConcurrentHashMap<UUID, Vec3> PlayerPositions = new ConcurrentHashMap<>();
    public ArrayList<AntiBuildZone> AntiBuildZones = new ArrayList<>();
    public ArrayList<OwnerZone> OwnerZones = new ArrayList<>();

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }

    public void Load(CompoundTag nbt) {
        PlayerPositions.clear();
        AntiBuildZones.clear();
        OwnerZones.clear();

        int version = nbt.getInt("version");

        CompoundTag AntiBuildZones = nbt.getCompound("AntiBuildZones");
        LoadAntiBuildZones(AntiBuildZones);

        CompoundTag OwnerZones = nbt.getCompound("OwnerZones");
        LoadOwnerZones(OwnerZones);
    }

    public CompoundTag Save(CompoundTag nbt) {
        nbt.putInt("version", version);

        CompoundTag antiBuildZones = SaveAntiBuildZones(new CompoundTag());
        nbt.put("AntiBuildZones", antiBuildZones);

        CompoundTag ownerZones = SaveOwnerZones(new CompoundTag());
        nbt.put("OwnerZones", ownerZones);

        return nbt;
    }

    private CompoundTag SaveAntiBuildZones(CompoundTag tag) {
        tag.putInt("count", AntiBuildZones.size());
        int i = 0;
        for (var zone : AntiBuildZones) {
            tag.put(Integer.toString((i++)), zone.serializeNBT());
        }
        return tag;
    }

    private void LoadAntiBuildZones(CompoundTag antiBuildZones) {
        int count = antiBuildZones.getInt("count");
        for (int i = 0; i < count; i++) {
            AntiBuildZone zone = new AntiBuildZone(antiBuildZones.getCompound(Integer.toString(i)));
            this.AntiBuildZones.add(zone);
        }
    }

    private CompoundTag SaveOwnerZones(CompoundTag tag) {
        tag.putInt("count", OwnerZones.size());
        int i = 0;
        for (var zone : OwnerZones) {
            tag.put(Integer.toString((i++)), zone.serializeNBT());
        }
        return tag;
    }

    private void LoadOwnerZones(CompoundTag antiBuildZones) {
        int count = antiBuildZones.getInt("count");
        for (int i = 0; i < count; i++) {
            OwnerZone zone = new OwnerZone(antiBuildZones.getCompound(Integer.toString(i)));
            this.OwnerZones.add(zone);
        }
    }

}
