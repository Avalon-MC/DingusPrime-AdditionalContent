package net.petercashel.dingusprimeacm.chair;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockEntity;
import net.petercashel.dingusprimeacm.cartshelf.container.CartShelfContainer;
import net.petercashel.dingusprimeacm.kubejs.kubejs.CardinalBlockJS;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ChairBlockJS  extends CardinalBlockJS {
    private final double offset;
    public ChairBlockJS(ChairBuilder p) {
        super(p);
        offset = p.offset;
    }


    public static double GetChairOffset(Block block) {

        if (block instanceof ChairBlockJS) {
            ChairBlockJS chair = (ChairBlockJS) block;
            return chair.offset;
        }

        return 0;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (chairEntity.getPassengers().size() != 0) {
            chairEntity.ejectPassengers();
        }
        chairEntity.kill();
        chairEntity = null;

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND)
        {
            if (isPlayerInRange(pPlayer,pPos)) {
                MountPlayerToChair(pLevel, pPos, this.offset, pPlayer);
                return InteractionResult.SUCCESS;
            }
        }


        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public ChairEntity chairEntity = null;
    public BlockPos chairLastPlayerBlockPos;

    public void MountPlayerToChair(Level world, BlockPos pos, double getChairOffset, Player player) {
        if (chairEntity == null) {
            chairEntity =  new ChairEntity(world, pos, getChairOffset);
            world.addFreshEntity(chairEntity);
        }
        if (chairEntity.getPassengers().size() != 0) {
            chairEntity.ejectPassengers();
        }

        chairLastPlayerBlockPos = player.blockPosition();
        player.startRiding(chairEntity);
    }

    /**
     * Returns whether or not the player is close enough to the block to be able to sit on it
     * @param player The player
     * @param pos The position of the block to sit on
     * @return true if the player is close enough, false otherwhise
     */
    private static boolean isPlayerInRange(Player player, BlockPos pos)
    {
        BlockPos playerPos = player.blockPosition();
        int blockReachDistance = 4;

        pos = pos.offset(0.5D, 0.5D, 0.5D);

        AABB range = new AABB(pos.getX() + blockReachDistance, pos.getY() + blockReachDistance, pos.getZ() + blockReachDistance, pos.getX() - blockReachDistance, pos.getY() - blockReachDistance, pos.getZ() - blockReachDistance);

        playerPos = playerPos.offset(0.5D, 0.5D, 0.5D);
        return range.minX <= playerPos.getX() && range.minY <= playerPos.getY() && range.minZ <= playerPos.getZ() && range.maxX >= playerPos.getX() && range.maxY >= playerPos.getY() && range.maxZ >= playerPos.getZ();
    }


    public static class ChairBuilder extends ChairBlockBuilder {
        public ChairBuilder(ResourceLocation i) {
            super(i);
        }

        public Block createObject() {
            return new ChairBlockJS(this);
        }
    }
}
