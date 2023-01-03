package net.petercashel.dingusprimeacm.world.zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;

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


    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return isPlayerOP(player);
    }

    @Override
    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        //Build / Destroy is OP only
        if (flag == ZonePermissions.ZonePermissionsEnum.Build || flag == ZonePermissions.ZonePermissionsEnum.Destroy) {
            return isPlayerOP(player);
        }

        //Allow rest
        return true;
    }
}
