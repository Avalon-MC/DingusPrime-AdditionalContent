package net.petercashel.dingusprimeacm.world.Zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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


    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return isPlayerOP(player);
    }
}
