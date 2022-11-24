package net.petercashel.dingusprimeacm.cartshelf.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.petercashel.dingusprimeacm.cabnet.CabnetBlockEntity;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

public class CartShelfBlockEntity extends CabnetBlockEntity {

    public CartShelfBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public CartShelfBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(dingusprimeKubeJSPlugin.CARTSHELF_BE, blockPos, blockState);
    }

}
