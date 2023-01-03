package net.petercashel.dingusprimeacm.world.zones.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.networking.NetworkUtils;

import java.util.UUID;

public class PlayerSelectionSession implements INBTSerializable<CompoundTag> {

    public BlockPos SelectionPositionA = null;
    public BlockPos SelectionPositionB = null;
    public AABB selectionBox = null;

    public void SetPlayerSelectionLeft(BlockState pState, Level pLevel, Player pPlayer, BlockPos pPos) {
        SelectionPositionA = pPos;
        ProcessPlayerSelection(this);
    }

    public void SetPlayerSelectionRight(UseOnContext pContext) {
        SelectionPositionB = pContext.getClickedPos();
        ProcessPlayerSelection(this);
    }

    public void expandSelection(Direction facing, int count, int backward) {
        boolean minIsA = (SelectionPositionA.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ) < SelectionPositionB.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ));
        selectionBox = selectionBox.expandTowards(facing.getStepX() * count, facing.getStepY() * count, facing.getStepZ() * count);

        if (backward != 0) {
            facing = facing.getOpposite();
            selectionBox = selectionBox.expandTowards(facing.getStepX() * backward, facing.getStepY() * backward, facing.getStepZ() * backward);
        }

        if (minIsA) {
            SelectionPositionA = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionB = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        } else {
            SelectionPositionB = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionA = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        }
    }
    public void contractSelection(Direction facing, int count, int backward) {
        boolean minIsA = (SelectionPositionA.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ) < SelectionPositionB.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ));
        selectionBox = selectionBox.contract(facing.getStepX() * count, facing.getStepY() * count, facing.getStepZ() * count);

        if (backward != 0) {
            facing = facing.getOpposite();
            selectionBox = selectionBox.contract(facing.getStepX() * backward, facing.getStepY() * backward, facing.getStepZ() * backward);
        }

        if (minIsA) {
            SelectionPositionA = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionB = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        } else {
            SelectionPositionB = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionA = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        }
    }

    public void shiftSelection(Direction facing, int count) {
        boolean minIsA = (SelectionPositionA.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ) < SelectionPositionB.distToCenterSqr(selectionBox.minX, selectionBox.minY, selectionBox.minZ));
        selectionBox = selectionBox.move(facing.getStepX() * count, facing.getStepY() * count, facing.getStepZ() * count);

        if (minIsA) {
            SelectionPositionA = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionB = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        } else {
            SelectionPositionB = new BlockPos(selectionBox.minX, selectionBox.minY, selectionBox.minZ);
            SelectionPositionA = new BlockPos(selectionBox.maxX - 1, selectionBox.maxY - 1, selectionBox.maxZ - 1);
        }
    }

    public void ClearSelections() {
        SelectionPositionA = null;
        SelectionPositionB = null;
        selectionBox = null;
    }

    public void ProcessPlayerSelection(PlayerSelectionSession session) {

        if (session.SelectionPositionA != null && session.SelectionPositionB != null) {
            selectionBox = new AABB(SelectionPositionA).minmax(new AABB(SelectionPositionB));
        } else {
            selectionBox = null;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        if (SelectionPositionA != null) {
            tag.put("SelectionPositionA", NetworkUtils.Serialization.SerializeBlockPos(SelectionPositionA));
        }
        if (SelectionPositionB != null) {
            tag.put("SelectionPositionB", NetworkUtils.Serialization.SerializeBlockPos(SelectionPositionB));
        }
        if (selectionBox != null) {
            tag.put("selectionBox", NetworkUtils.Serialization.SerializeAABB(selectionBox));
        }

        return tag;
    }


    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag != null) {

            if (tag.contains("SelectionPositionA")) {
                SelectionPositionA = NetworkUtils.Serialization.DeserializeBlockPos(tag.getCompound("SelectionPositionA"));
            }
            if (tag.contains("SelectionPositionB")) {
                SelectionPositionB = NetworkUtils.Serialization.DeserializeBlockPos(tag.getCompound("SelectionPositionB"));
            }
            if (tag.contains("selectionBox")) {
                selectionBox = NetworkUtils.Serialization.DeserializeAABB(tag.getCompound("selectionBox"));
            }

        }
    }

}
