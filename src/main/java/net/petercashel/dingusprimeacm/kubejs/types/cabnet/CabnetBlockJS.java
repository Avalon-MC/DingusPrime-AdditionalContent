package net.petercashel.dingusprimeacm.kubejs.types.cabnet;

import dev.latvian.mods.kubejs.block.custom.HorizontalDirectionalBlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class CabnetBlockJS extends HorizontalDirectionalBlockBuilder.HorizontalDirectionalBlockJS implements EntityBlock {
    public CabnetBlockJS(CabnetBuilder p) {
        super(p);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CabnetBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND)
        {
            //Temp, Randomise carts
            CabnetBlockEntity csbe = (CabnetBlockEntity) pLevel.getBlockEntity(pPos);
            if (csbe != null) {
                csbe.MarkDirtySaveData();

                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return MutableComponent.create(pState.getBlock().getName().getContents());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        var menu = new CabnetContainer(windowId, pPos, playerInventory, playerEntity);
                        return menu;
                    }
                };
                NetworkHooks.openGui((ServerPlayer) pPlayer, containerProvider, csbe.getBlockPos());


                return InteractionResult.SUCCESS;
            }
        }


        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
    }


    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide()) {
            CabnetBlockEntity csbe = (CabnetBlockEntity) pLevel.getBlockEntity(pPos);

            var cap = csbe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

            if (cap.isPresent()) {
                var ItemHandler = cap.resolve().get();

                int slots = ItemHandler.getSlots();

                for (int i = 0; i < slots; i++) {
                    var ItemStack = ItemHandler.getStackInSlot(i);
                    pLevel.addFreshEntity(new ItemEntity(pPlayer.level, pPlayer.position().x, pPlayer.position().y, pPlayer.position().z, ItemStack));
                }
            }
        }

        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public static class CabnetBuilder extends HorizontalDirectionalBlockBuilder {
        public CabnetBuilder(ResourceLocation i) {
            super(i);
        }

        public Block createObject() {
            return new CabnetBlockJS(this);
        }
    }
}
