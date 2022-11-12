package net.petercashel.dingusprimeacm.kubejs.kubejs;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

import static net.petercashel.dingusprimeacm.kubejs.kubejs.CardinalBlockBuilder.createShape;

//Based on 1.19 KubeJS code for HoriziontalDirectional
public class CardinalBlockJS extends BasicBlockJS {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public final Map<Direction, VoxelShape> shapes = new HashMap<>();

    public CardinalBlockJS(CardinalBlockBuilder p) {
        super(p);

        //Override shape
        try {
            Field f = BasicBlockJS.class.getDeclaredField("shape");
            f.setAccessible(true);
            f.set(this, createShape(p.customShape));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (hasCustomShape()) {
            Direction.Plane.HORIZONTAL.forEach(direction -> shapes.put(direction, rotateShape(shape, direction)));
        }
    }

        private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
            List<AABB> newShapes = new ArrayList<>();

            switch (direction) {
                case NORTH -> {return shape;}
                case SOUTH -> shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(1D - x2, y1, 1D - z2, 1D - x1, y2, 1D - z1)));
                case WEST -> shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(z1, y1, 1D - x2, z2, y2, 1D - x1)));
                case EAST -> shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(1D - z2, y1, x1, 1D - z1, y2, x2)));
                default -> throw new IllegalArgumentException("Cannot rotate around direction " + direction.getName());
            }
            return createShape(newShapes);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
            super.createBlockStateDefinition(builder);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            var state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());

            if (blockBuilder.waterlogged) {
                state = state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
            }

            return state;
        }

        private boolean hasCustomShape() {
            return shape != Shapes.block();
        }

        @Override
        @Deprecated
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return hasCustomShape() ? shapes.get(state.getValue(FACING)) : shape;
        }

    public BlockBuilder getBlockBuilderKJS() {
        return this.blockBuilder;
    }

    public static class CardinalBuilder extends CardinalBlockBuilder {
        public CardinalBuilder(ResourceLocation i) {
            super(i);
        }

        public Block createObject() {
            return new CardinalBlockJS(this);
        }
    }

}
