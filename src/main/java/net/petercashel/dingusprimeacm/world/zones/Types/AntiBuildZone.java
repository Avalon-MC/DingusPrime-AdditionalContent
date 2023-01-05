package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;

import java.util.EnumSet;

public class AntiBuildZone extends BaseZone {

    public AntiBuildZone(BlockPos startPos, double radius) {
        super(startPos, radius);
    }

    public AntiBuildZone(BlockPos startPos, BlockPos endPos) {
        super(startPos, endPos);
    }

    public AntiBuildZone(Vec3 startPos, double radius) {
        super(startPos, radius);
    }

    public AntiBuildZone(Vec3 startPos, Vec3 endPos) {
        super(startPos, endPos);
    }

    public AntiBuildZone(CompoundTag compound) {
        super();
        this.deserializeNBT(compound);
    }

    public AntiBuildZone(AABB collisionBox) {
        super(collisionBox);
    }

    public ZonePermissions PublicPerms = new ZonePermissions(EnumSet.allOf(ZonePermissions.ZonePermissionsEnum.class), true, true);

    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return HasPermission(pos, player, ZonePermissions.ZonePermissionsEnum.Build);
    }

    @Override
    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        return isPlayerOP(player) || (PublicPerms.hasPermissionFlag(flag));
    }

    @Override
    public boolean HasPermission(Vec3 pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        return isPlayerOP(player) || (PublicPerms.hasPermissionFlag(flag));
    }

    @Override
    public boolean HasPublicPermission(Vec3 pos, ZonePermissions.ZonePermissionsEnum flag) {
        return (PublicPerms.hasPermissionFlag(flag));
    }

    static int version = 1;

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        OwnerUUID = nbt.getUUID("OwnerUUID");
        int version = 0;

        if (nbt.contains("version")) {
            version = nbt.getInt("version");
        }

        if (version == 1) {
            CompoundTag PublicPerms = nbt.getCompound("PublicPerms");
            this.PublicPerms.deserializeNBT(PublicPerms);
        }

    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = (CompoundTag) super.serializeNBT();
        tag.putInt("version", version);

        tag.put("PublicPerms", PublicPerms.serializeNBT());


        return tag;
    }

}
