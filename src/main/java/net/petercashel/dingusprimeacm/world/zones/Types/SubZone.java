package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;

import java.util.UUID;

public class SubZone extends BaseOwnableZone {

    public OwnerZone ParentZone;
    public UUID ParentZoneUUID;

    public SubZone(CompoundTag compound) {
        super();
        this.deserializeNBT(compound);
    }

    public SubZone(AABB box) {
        super(box);
    }
    public SubZone(AABB box, OwnerZone parentZone) {
        super(box);
        this.ParentZone = parentZone;
        this.ParentZoneUUID = parentZone.ZoneUUID;
    }

    static int version = 1;

    @Override
    public boolean isOwner(Player player) {
        return player.getUUID().equals(OwnerUUID) || player.getUUID().equals(ParentZone.OwnerUUID);
    }

    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                (PublicPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build));
    }

    @Override
    public boolean HasPermission(Vec3 pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(flag)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(flag)) ||
                (PublicPerms.hasPermissionFlag(flag));
    }

    @Override
    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
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


        return tag;
    }
}
