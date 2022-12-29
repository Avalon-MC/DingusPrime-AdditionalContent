package net.petercashel.dingusprimeacm.world.Zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.Zones.ZonePermissions;

import java.util.ArrayList;
import java.util.UUID;

public class OwnerZone extends BaseZone {

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

    public ArrayList<UUID> MemberUUIDs = new ArrayList<>();
    public ZonePermissions MemberPerms = new ZonePermissions();

    public ArrayList<UUID> AllyUUIDs = new ArrayList<>();
    public ZonePermissions AllyPerms = new ZonePermissions();

    public ZonePermissions PublicPerms = new ZonePermissions();

    static int version = 1;

    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build)) ||
                 (PublicPerms.hasPermissionFlag(ZonePermissions.ZonePermissionsEnum.Build));
    }

    @Override
    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        return isOwner(player) || isPlayerOP(player) ||
                (isMember(player) && MemberPerms.hasPermissionFlag(flag)) ||
                (isAlly(player) && AllyPerms.hasPermissionFlag(flag)) ||
                (PublicPerms.hasPermissionFlag(flag));
    }

    boolean isMember(Player player) {
        return MemberUUIDs.contains(player.getUUID());
    }
    boolean isAlly(Player player) {
        return AllyUUIDs.contains(player.getUUID());
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

    private void LoadAllies(CompoundTag allyUUIDs) {
        int count = allyUUIDs.getInt("count");
        for (int i = 0; i < count; i++) {
            this.AllyUUIDs.add(allyUUIDs.getUUID(Integer.toString(i)));
        }
    }

    private void LoadMembers(CompoundTag memberUUIDs) {
        int count = memberUUIDs.getInt("count");
        for (int i = 0; i < count; i++) {
            this.MemberUUIDs.add(memberUUIDs.getUUID(Integer.toString(i)));
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

    private CompoundTag SaveMembers(CompoundTag tag) {
        tag.putInt("count", MemberUUIDs.size());
        int i = 0;
        for (var uuid : MemberUUIDs) {
            tag.putUUID(Integer.toString((i++)), uuid);
        }
        return tag;
    }

    private CompoundTag SaveAllies(CompoundTag tag) {
        tag.putInt("count", AllyUUIDs.size());
        int i = 0;
        for (var uuid : AllyUUIDs) {
            tag.putUUID(Integer.toString((i++)), uuid);
        }
        return tag;
    }

    public boolean isMember(UUID uuid) {
        return this.MemberUUIDs.contains(uuid);
    }
    public boolean isAlly(UUID uuid) {
        return this.AllyUUIDs.contains(uuid);
    }
}
