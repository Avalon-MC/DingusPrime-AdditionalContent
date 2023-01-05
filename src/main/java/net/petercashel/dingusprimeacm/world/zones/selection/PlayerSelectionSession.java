package net.petercashel.dingusprimeacm.world.zones.selection;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.util.INBTSerializable;
import net.petercashel.dingusprimeacm.networking.NetworkUtils;

import java.util.UUID;

public class PlayerSelectionSession implements INBTSerializable<CompoundTag> {

    public BlockPos SelectionPositionA = null;
    public BlockPos SelectionPositionB = null;
    public AABB selectionBox = null;

    public void SetPlayerSelectionLeft(BlockState pState, Level pLevel, Player pPlayer, BlockPos pPos) {
        SelectionPositionA = pPos;
        ServerPlayer serverPlayer = (ServerPlayer) pPlayer;
        serverPlayer.sendMessage(new TextComponent("First Position Selected"), ChatType.GAME_INFO, Util.NIL_UUID);
        ProcessPlayerSelection(this, pPlayer);
    }

    public void SetPlayerSelectionRight(UseOnContext pContext) {
        SelectionPositionB = pContext.getClickedPos();
        ServerPlayer serverPlayer = (ServerPlayer) pContext.getPlayer();
        serverPlayer.sendMessage(new TextComponent("Second Position Selected"), ChatType.GAME_INFO, Util.NIL_UUID);
        ProcessPlayerSelection(this, pContext.getPlayer());
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

    public void ClearSelections(Player pPlayer) {
        SelectionPositionA = null;
        SelectionPositionB = null;
        selectionBox = null;
        ServerPlayer serverPlayer = (ServerPlayer) pPlayer;
        serverPlayer.sendMessage(new TextComponent("Selection Cleared"), ChatType.GAME_INFO, Util.NIL_UUID);
    }

    public void ProcessPlayerSelection(PlayerSelectionSession session, Player pPlayer) {

        if (session.SelectionPositionA != null && session.SelectionPositionB != null) {
            selectionBox = new AABB(SelectionPositionA).minmax(new AABB(SelectionPositionB));
            ServerPlayer serverPlayer = (ServerPlayer) pPlayer;
            serverPlayer.sendMessage(new TextComponent("Selection: " +
                    (Math.abs( selectionBox.maxX - selectionBox.minX)) + " x " +
                    (Math.abs( selectionBox.maxY - selectionBox.minY)) + " x " +
                    (Math.abs( selectionBox.maxZ - selectionBox.minZ))), ChatType.GAME_INFO, Util.NIL_UUID);
        } else {
            selectionBox = null;
            ServerPlayer serverPlayer = (ServerPlayer) pPlayer;
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
