package net.petercashel.dingusprimeacm.world.Zones.Types;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public abstract class BaseZone implements INBTSerializable {

    public UUID OwnerUUID;
    public AABB CollisionBox;

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
        OwnerUUID = Util.NIL_UUID;
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


    public AABB getCollisionBox() {
        return CollisionBox;
    }

    public void setCollisionBox(AABB collisionBox) {
        CollisionBox = collisionBox;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("AABB_min_X", getCollisionBox().minX);
        tag.putDouble("AABB_min_Y", getCollisionBox().minY);
        tag.putDouble("AABB_min_Z", getCollisionBox().minZ);
        tag.putDouble("AABB_max_X", getCollisionBox().maxX);
        tag.putDouble("AABB_max_Y", getCollisionBox().maxY);
        tag.putDouble("AABB_max_Z", getCollisionBox().maxZ);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        if (tag != null) {
            CollisionBox.setMinX(tag.getDouble("AABB_min_X"));
            CollisionBox.setMinX(tag.getDouble("AABB_min_Y"));
            CollisionBox.setMinX(tag.getDouble("AABB_min_Z"));
            CollisionBox.setMinX(tag.getDouble("AABB_max_X"));
            CollisionBox.setMinX(tag.getDouble("AABB_max_Y"));
            CollisionBox.setMinX(tag.getDouble("AABB_max_Z"));
        }
    }

    public boolean isPlayerOP(Player player) {
        return player.hasPermissions(3);
    }

    boolean isOwner(Player player) {
        return player.getUUID().equals(OwnerUUID);
    }

    public boolean Contains(BlockPos startPos) {
        return CollisionBox.contains(startPos.getX(), startPos.getY(), startPos.getZ());
    }
    public boolean Contains(Vec3 startPos) {
        return CollisionBox.contains(startPos.x, startPos.y, startPos.z);
    }

    public abstract boolean CanBuild(BlockPos pos, Player player);

    public BaseZone SetOwner(UUID uuid) {
        this.OwnerUUID = uuid;
        return this;
    }
}
