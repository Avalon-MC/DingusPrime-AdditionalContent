package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;

import java.util.ArrayList;
import java.util.UUID;

public abstract class BaseOwnableZone extends BaseZone {
    public BaseOwnableZone() {
    }

    public BaseOwnableZone(BlockPos startPos, double radius) {
        super(startPos, radius);
    }

    public BaseOwnableZone(Vec3 startPos, double radius) {
        super(startPos, radius);
    }

    public BaseOwnableZone(BlockPos startPos, BlockPos endPos) {
        super(startPos, endPos);
    }

    public BaseOwnableZone(Vec3 startPos, Vec3 endPos) {
        super(startPos, endPos);
    }

    public BaseOwnableZone(BlockPos startPos, double radius, ServerPlayer player) {
        super(startPos, radius, player);
    }

    public BaseOwnableZone(BlockPos startPos, BlockPos endPos, ServerPlayer player) {
        super(startPos, endPos, player);
    }

    public BaseOwnableZone(Vec3 startPos, Vec3 endPos, ServerPlayer player) {
        super(startPos, endPos, player);
    }


    public ArrayList<UUID> MemberUUIDs = new ArrayList<>();
    public ZonePermissions MemberPerms = new ZonePermissions();

    public ArrayList<UUID> AllyUUIDs = new ArrayList<>();
    public ZonePermissions AllyPerms = new ZonePermissions();

    public ZonePermissions PublicPerms = new ZonePermissions();

    public BaseOwnableZone(AABB box) {
        super(box);
    }
    public BaseOwnableZone(AABB box, ServerPlayer player) {
        super(box, player);
    }


    public boolean isMember(Player player) {
        return MemberUUIDs.contains(player.getUUID());
    }
    public boolean isAlly(Player player) {
        return AllyUUIDs.contains(player.getUUID());
    }

    public boolean isMember(UUID uuid) {
        return this.MemberUUIDs.contains(uuid);
    }

    public boolean isAlly(UUID uuid) {
        return this.AllyUUIDs.contains(uuid);
    }


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


    void LoadAllies(CompoundTag allyUUIDs) {
        int count = allyUUIDs.getInt("count");
        for (int i = 0; i < count; i++) {
            this.AllyUUIDs.add(allyUUIDs.getUUID(Integer.toString(i)));
        }
    }

    void LoadMembers(CompoundTag memberUUIDs) {
        int count = memberUUIDs.getInt("count");
        for (int i = 0; i < count; i++) {
            this.MemberUUIDs.add(memberUUIDs.getUUID(Integer.toString(i)));
        }
    }


    CompoundTag SaveMembers(CompoundTag tag) {
        tag.putInt("count", MemberUUIDs.size());
        int i = 0;
        for (var uuid : MemberUUIDs) {
            tag.putUUID(Integer.toString((i++)), uuid);
        }
        return tag;
    }

    CompoundTag SaveAllies(CompoundTag tag) {
        tag.putInt("count", AllyUUIDs.size());
        int i = 0;
        for (var uuid : AllyUUIDs) {
            tag.putUUID(Integer.toString((i++)), uuid);
        }
        return tag;
    }


}
