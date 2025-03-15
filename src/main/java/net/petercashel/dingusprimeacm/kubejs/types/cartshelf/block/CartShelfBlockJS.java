package net.petercashel.dingusprimeacm.kubejs.types.cartshelf.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.network.NetworkHooks;
import net.petercashel.dingusprimeacm.kubejs.types.cabnet.CabnetBlockJS;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.container.CartShelfContainer;
import org.jetbrains.annotations.Nullable;

public class CartShelfBlockJS  extends CabnetBlockJS implements EntityBlock {
    public CartShelfBlockJS(CartShelfBuilder p) {
        super(p);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CartShelfBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND)
        {
            //Temp, Randomise carts
            CartShelfBlockEntity csbe = (CartShelfBlockEntity) pLevel.getBlockEntity(pPos);
            if (csbe != null) {
                csbe.MarkDirtySaveData();

                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Cart Shelf");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        var menu = new CartShelfContainer(windowId, pPos, playerInventory, playerEntity);
                        return menu;
                    }
                };
                NetworkHooks.openGui((ServerPlayer) pPlayer, containerProvider, csbe.getBlockPos());


                return InteractionResult.SUCCESS;
            }
        }


        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }



    public static class CartShelfBuilder extends CabnetBuilder {
        public CartShelfBuilder(ResourceLocation i) {
            super(i);
        }

        public Block createObject() {
            return new CartShelfBlockJS(this);
        }
    }
}
