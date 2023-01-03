package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.networking.NetworkUtils;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;
import java.util.UUID;

public abstract class BaseZone implements INBTSerializable<CompoundTag> {

    public UUID ZoneUUID = UUID.randomUUID();

    public UUID OwnerUUID;
    public AABB CollisionBox;
    public String ZoneName = RandomStringUtils.randomAlphanumeric(16).toLowerCase(Locale.ROOT);

    protected BaseZone() {
        CollisionBox = new AABB(Vec3.ZERO, Vec3.ZERO);
        OwnerUUID = Util.NIL_UUID;
    }

    public BaseZone(BlockPos startPos, double radius) {
        CollisionBox = new AABB(startPos).inflate(radius);
        OwnerUUID = Util.NIL_UUID;
    }

    public BaseZone(Vec3 startPos, double radius) {
        CollisionBox = new AABB(startPos, startPos).inflate(radius);
        OwnerUUID = Util.NIL_UUID;
    }

    public BaseZone(BlockPos startPos, BlockPos endPos) {
        CollisionBox = new AABB(startPos, endPos);
        OwnerUUID = Util.NIL_UUID;
    }

    public BaseZone(Vec3 startPos, Vec3 endPos) {
        CollisionBox = new AABB(startPos, endPos);
    }

    public BaseZone(BlockPos startPos, double radius, ServerPlayer player) {
        CollisionBox = new AABB(startPos).inflate(radius);
        OwnerUUID = player.getUUID();
    }

    public BaseZone(BlockPos startPos, BlockPos endPos, ServerPlayer player) {
        CollisionBox = new AABB(startPos, endPos);
        OwnerUUID = player.getUUID();
    }

    public BaseZone(Vec3 startPos, Vec3 endPos, ServerPlayer player) {
        CollisionBox = new AABB(startPos, endPos);
        OwnerUUID = player.getUUID();
    }

    public BaseZone(AABB collisionBox) {
        CollisionBox = collisionBox;
        OwnerUUID = Util.NIL_UUID;
    }

    public BaseZone(AABB collisionBox, ServerPlayer player) {
        CollisionBox = collisionBox;
        OwnerUUID = player.getUUID();
    }


    public AABB getCollisionBox() {
        return CollisionBox;
    }

    public void setCollisionBox(AABB collisionBox) {
        CollisionBox = collisionBox;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("AABB_min_X", getCollisionBox().minX);
        tag.putDouble("AABB_min_Y", getCollisionBox().minY);
        tag.putDouble("AABB_min_Z", getCollisionBox().minZ);
        tag.putDouble("AABB_max_X", getCollisionBox().maxX);
        tag.putDouble("AABB_max_Y", getCollisionBox().maxY);
        tag.putDouble("AABB_max_Z", getCollisionBox().maxZ);

        tag.putString("ZoneName", ZoneName);
        tag.putUUID("ZoneUUID", ZoneUUID);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag != null) {
            CollisionBox = new AABB(
                    tag.getDouble("AABB_min_X"),
                    tag.getDouble("AABB_min_Y"),
                    tag.getDouble("AABB_min_Z"),
                    tag.getDouble("AABB_max_X"),
                    tag.getDouble("AABB_max_Y"),
                    tag.getDouble("AABB_max_Z")
            );

            if (tag.contains("ZoneName")) {
                ZoneName = tag.getString("ZoneName");
            }

            if (tag.contains("ZoneUUID")) {
                ZoneUUID = tag.getUUID("ZoneUUID");
            } else {
                ZoneUUID = UUID.randomUUID();
            }
        }
    }

    public boolean isPlayerOP(Player player) {
        return player.hasPermissions(3);
    }

    public boolean isOwner(Player player) {
        return player.getUUID().equals(OwnerUUID);
    }


    public boolean Contains(BlockPos startPos) {
        return CollisionBox.contains(startPos.getX(), startPos.getY(), startPos.getZ());
    }
    public boolean Contains(Vec3 startPos) {
        return CollisionBox.contains(startPos.x, startPos.y, startPos.z);
    }

    public abstract boolean CanBuild(BlockPos pos, Player player);

    public abstract boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag);

    public BaseZone SetOwner(UUID uuid) {
        this.OwnerUUID = uuid;
        return this;
    }

    public BaseZone SetName(String playerName) {
        ZoneName = playerName;
        return this;
    }
}
