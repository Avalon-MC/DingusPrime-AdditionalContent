package net.petercashel.dingusprimeacm.kubejs.types.lamps;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.petercashel.dingusprimeacm.kubejs.basictypes.ExtendedBlockBuilder;


public class LampBlockJS extends BasicBlockJS {

    public static final int Light = 64;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final boolean isLampTop;
    private boolean isOn = false;


    public LampBlockJS(LampBuilder p) {
        super(p);
        this.isLampTop = p.isTop;
        if (isLampTop) {

        }
    }

    @Override
    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        boolean flag = pState.getValue(LIT);
        if (!flag) return 0;
        return Light;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        boolean flag = state.getValue(LIT);
        if (!flag) return 0;
        return Light;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            if (isLampTop) {
                var newState = pState.cycle(LIT);
                pLevel.setBlock(pPos, newState, 1);
                pLevel.sendBlockUpdated(pPos, pLevel.getBlockState(pPos), pLevel.getBlockState(pPos), 2);
                pLevel.scheduleTick(pPos, pLevel.getBlockState(pPos).getBlock(), 4);
                return InteractionResult.SUCCESS;
            } else {
                BlockState aboveState = pLevel.getBlockState(pPos.above());
                if (!aboveState.isAir()) {
                    if (aboveState.getBlock() instanceof LampBlockJS) {
                        LampBlockJS block = (LampBlockJS) aboveState.getBlock();
                        if (block != null) {
                            return block.HandlePost(aboveState, pLevel, pPos.above(), 0);
                        }
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult HandlePost(BlockState pState, Level pLevel, BlockPos pPos, int depth) {
        if (depth > 8) {
            return InteractionResult.PASS;
        }

        if (isLampTop) {
            //Switch state
            var newState = pState.cycle(LIT);
            pLevel.setBlock(pPos, newState, 1);
            pLevel.sendBlockUpdated(pPos, pLevel.getBlockState(pPos), pLevel.getBlockState(pPos), 2);
            pLevel.scheduleTick(pPos, pLevel.getBlockState(pPos).getBlock(), 4);
            return InteractionResult.SUCCESS;
        } else {
            BlockState aboveState = pLevel.getBlockState(pPos.above());
            if (!aboveState.isAir()) {
                if (aboveState.getBlock() instanceof LampBlockJS) {
                    LampBlockJS block = (LampBlockJS) aboveState.getBlock();
                    if (block != null) {
                        return block.HandlePost(aboveState, pLevel, pPos.above(), depth + 1);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        var superState = super.getStateForPlacement(context);
        return superState.setValue(LIT, Boolean.FALSE);
    }


    public BlockBuilder getBlockBuilderKJS() {
        return this.blockBuilder;
    }

    public static abstract class LampBuilder extends ExtendedBlockBuilder {
        public LampBuilder(ResourceLocation i) {
            super(i);
        }
        public boolean isTop = false;
    }

    public static class LampTopBuilder extends LampBuilder {
        public LampTopBuilder(ResourceLocation i) {
            super(i);
            isTop = true;
            this.lightLevel(0.9f);
            defaultCutout();
        }

        public Block createObject() {
            return new LampBlockJS(this);
        }
    }

    public static class LampPostBuilder extends LampBuilder {
        public LampPostBuilder(ResourceLocation i) {
            super(i);
            isTop = false;
            defaultCutout();
        }

        public Block createObject() {
            return new LampBlockJS(this);
        }
    }

}
