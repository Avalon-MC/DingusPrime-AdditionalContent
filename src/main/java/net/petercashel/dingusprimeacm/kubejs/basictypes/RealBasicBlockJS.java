package net.petercashel.dingusprimeacm.kubejs.basictypes;


import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.core.BlockBuilderProvider;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RealBasicBlockJS extends Block implements BlockBuilderProvider {
    public final BlockBuilder blockBuilder;
    public final VoxelShape shape;

    public RealBasicBlockJS(BlockBuilder p) {
        super(p.createProperties());
        this.blockBuilder = p;
        this.shape = p.createShape();
        if (this.blockBuilder.waterlogged) {
            this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BlockStateProperties.WATERLOGGED, false));
        }

    }

    public BlockBuilder getBlockBuilderKJS() {
        return this.blockBuilder;
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        BuilderBase var3 = RegistryObjectBuilderTypes.BLOCK.getCurrent();
        if (var3 instanceof BlockBuilder) {
            BlockBuilder current = (BlockBuilder)var3;
            if (current.waterlogged) {
                builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return this.blockBuilder.waterlogged && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.blockBuilder.waterlogged ? this.defaultBlockState() : (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    /** @deprecated */
    @Deprecated
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (this.blockBuilder.waterlogged && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return state;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return this.blockBuilder.transparent || !this.blockBuilder.waterlogged || !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED);
    }

    /** @deprecated */
    @Deprecated
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (this.blockBuilder.randomTickCallback != null) {
            BlockContainerJS containerJS = new BlockContainerJS(level, pos);

            try {
                this.blockBuilder.randomTickCallback.accept(new RandomTickCallbackJS(containerJS, random));
            } catch (Exception var7) {
                KubeJS.LOGGER.error("Error while random ticking custom block {}: {}", this, var7);
            }
        }

    }

    public boolean isRandomlyTicking(BlockState state) {
        return this.blockBuilder.randomTickCallback != null;
    }

    /** @deprecated */
    @Deprecated
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return this.blockBuilder.transparent ? Shapes.empty() : super.getVisualShape(state, level, pos, ctx);
    }

    /** @deprecated */
    @Deprecated
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return this.blockBuilder.transparent ? 1.0F : super.getShadeBrightness(state, level, pos);
    }

    /** @deprecated */
    @Deprecated
    public boolean skipRendering(BlockState state, BlockState state2, Direction direction) {
        return this.blockBuilder.transparent ? state2.is(this) || super.skipRendering(state, state2, direction) : super.skipRendering(state, state2, direction);
    }

    public static class Builder extends BlockBuilder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        public Block createObject() {
            return new RealBasicBlockJS(this);
        }
    }
}
