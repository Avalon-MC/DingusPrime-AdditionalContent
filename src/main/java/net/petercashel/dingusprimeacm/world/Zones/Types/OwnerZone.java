package net.petercashel.dingusprimeacm.world.Zones.Types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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

    @Override
    public boolean CanBuild(BlockPos pos, Player player) {
        return isPlayerOP(player) || isOwner(player);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        super.deserializeNBT(nbt);
        CompoundTag compoundTag = (CompoundTag) nbt;
        OwnerUUID = compoundTag.getUUID("OwnerUUID");
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = (CompoundTag) super.serializeNBT();
        tag.putUUID("OwnerUUID", OwnerUUID);
        return tag;
    }
}
