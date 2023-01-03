package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;

import java.util.ArrayList;

public class OwnerZone extends BaseOwnableZone {

    public OwnerZone(BlockPos startPos, double radius) {
        super(startPos, radius);
    }

    public OwnerZone(BlockPos startPos, BlockPos endPos) {
        super(startPos, endPos);
    }

    public OwnerZone(Vec3 startPos, double radius) {
        super(startPos, radius);
    }

    public OwnerZone(Vec3 startPos, Vec3 endPos) {
        super(startPos, endPos);
    }

    public OwnerZone(CompoundTag compound) {
        super();
        this.deserializeNBT(compound);
    }

    public OwnerZone(AABB box) {
        super(box);
    }
    public OwnerZone(AABB box, ServerPlayer player) {
        super(box, player);
    }


    public ArrayList<SubZone> SubZones = new ArrayList<>();

    static int version = 2;


    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        SubZone zone = GetSubzoneForPosition(pos);
        if (zone != null) {
            return zone.CanBuild(pos, player);
        }

        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                 (PublicPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build));
    }

    public SubZone GetSubzoneForPosition(BlockPos pos) {
        for (SubZone zone : SubZones) {
            if (zone.CollisionBox.contains(pos.getX(), pos.getY(), pos.getZ())) return zone;
        }
        return null;
    }

    @Override
    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        SubZone zone = GetSubzoneForPosition(pos);
        if (zone != null) {
            return zone.HasPermission(pos, player, flag);
        }

        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(flag)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(flag)) ||
                (PublicPerms.hasPermissionFlag(flag));
    }


    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        OwnerUUID = nbt.getUUID("OwnerUUID");
        int version = 0;

        if (nbt.contains("version")) {
            version = nbt.getInt("version");
        }

        if (version == 1) {
            //Should be but sanity.
            MemberUUIDs.clear();
            AllyUUIDs.clear();

            CompoundTag MemberPerms = nbt.getCompound("MemberPerms");
            CompoundTag AllyPerms = nbt.getCompound("AllyPerms");
            CompoundTag PublicPerms = nbt.getCompound("PublicPerms");
            this.MemberPerms.deserializeNBT(MemberPerms);
            this.AllyPerms.deserializeNBT(AllyPerms);
            this.PublicPerms.deserializeNBT(PublicPerms);

            CompoundTag MemberUUIDs = nbt.getCompound("MemberUUIDs");
            CompoundTag AllyUUIDs = nbt.getCompound("AllyUUIDs");

            LoadMembers(MemberUUIDs);
            LoadAllies(AllyUUIDs);
        }

        if (version == 2) {
            CompoundTag SubZones = nbt.getCompound("SubZones");
            LoadSubZones(SubZones);
        }

    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = (CompoundTag) super.serializeNBT();
        tag.putUUID("OwnerUUID", OwnerUUID);

        tag.putInt("version", version);

        CompoundTag memberUUIDs = SaveMembers(new CompoundTag());
        tag.put("MemberUUIDs", memberUUIDs);

        CompoundTag allyUUIDs = SaveAllies(new CompoundTag());
        tag.put("AllyUUIDs", allyUUIDs);

        tag.put("MemberPerms", MemberPerms.serializeNBT());
        tag.put("AllyPerms", AllyPerms.serializeNBT());
        tag.put("PublicPerms", PublicPerms.serializeNBT());

        CompoundTag subZones = SaveSubZones(new CompoundTag());
        tag.put("SubZones", subZones);

        return tag;
    }


    //SubZones

    private CompoundTag SaveSubZones(CompoundTag tag) {
        tag.putInt("count", SubZones.size());
        int i = 0;
        for (var zone : SubZones) {
            tag.put(Integer.toString((i++)), zone.serializeNBT());
        }
        return tag;
    }

    private void LoadSubZones(CompoundTag antiBuildZones) {
        int count = antiBuildZones.getInt("count");
        for (int i = 0; i < count; i++) {
            SubZone zone = new SubZone(antiBuildZones.getCompound(Integer.toString(i)));
            zone.ParentZoneUUID = this.ZoneUUID;
            zone.ParentZone = this;
            this.SubZones.add(zone);
        }
    }
}
