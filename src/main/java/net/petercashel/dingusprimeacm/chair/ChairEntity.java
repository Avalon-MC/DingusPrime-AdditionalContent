package net.petercashel.dingusprimeacm.chair;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.dingusprimeacm;

public class ChairEntity extends Entity {

    private BlockPos chairBlockPos;

    public ChairEntity(EntityType<ChairEntity> type, Level world)
    {
        super(type, world);
        chairBlockPos = this.blockPosition();
    }

    public ChairEntity(Level world, BlockPos pos, double offset)
    {
        super(dingusprimeacm.CHAIR_ENTITY_TYPE.get(), world);
        setPos(pos.getX() + 0.5D, (pos.getY() + 0.25D) + offset, pos.getZ() + 0.5D);
        noPhysics = true;
        chairBlockPos = pos;
    }


    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger)
    {
        if(passenger instanceof Player player)
        {
            if (chairBlockPos == BlockPos.ZERO) {
                chairBlockPos = this.blockPosition().above();
            }

            if (this.level.getBlockState(chairBlockPos).getBlock() instanceof ChairBlockJS) {
                ChairBlockJS chair = (ChairBlockJS) this.level.getBlockState(chairBlockPos).getBlock();

                if (chair != null) {
                    BlockPos pos = chair.chairLastPlayerBlockPos;

                    if(pos != null)
                    {
                        Vec3 resetPosition = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                        BlockPos belowResetPos = new BlockPos(resetPosition.x, resetPosition.y - 1, resetPosition.z);

                        discard();

                        if(!player.level.getBlockState(belowResetPos).isFaceSturdy(level, belowResetPos, Direction.UP, SupportType.FULL))
                            return new Vec3(resetPosition.x, resetPosition.y + 1, resetPosition.z);
                        else
                            return resetPosition;
                    }
                }
            } else {

                //OK! Fallback time
                BlockPos pos = this.blockPosition().above();
                if(pos != null)
                {
                    Vec3 resetPosition = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    BlockPos belowResetPos = new BlockPos(resetPosition.x, resetPosition.y - 1, resetPosition.z);

                    discard();

                    if(!player.level.getBlockState(belowResetPos).isFaceSturdy(level, belowResetPos, Direction.UP, SupportType.FULL))
                        return new Vec3(resetPosition.x, resetPosition.y + 1, resetPosition.z);
                    else
                        return resetPosition;
                }
            }
        }

        discard();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove(RemovalReason reason)
    {
        ChairBlockJS chair = (ChairBlockJS) this.level.getBlockState(chairBlockPos).getBlock();
        chair.chairEntity = null;
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return new ClientboundAddEntityPacket(this);
    }

}
