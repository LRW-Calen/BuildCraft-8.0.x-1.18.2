/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.lib.block;

import buildcraft.api.blocks.ICustomRotationHandler;
import buildcraft.api.properties.BuildCraftProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public abstract class BlockMarkerBase extends BlockBCTile_Neptune implements ICustomRotationHandler {
    private static final Map<Direction, VoxelShape> BOUNDING_BOXES = new EnumMap<>(Direction.class);

    private static final double halfWidth = 0.1;
    private static final double h = 0.65;
    // Little variables to make reading a *bit* more sane
    private static final double nw = 0.5 - halfWidth;
    private static final double pw = 0.5 + halfWidth;
    private static final double ih = 1 - h;
    private static final VoxelShape BOUNDING_BOX_DOWN = Shapes.box(nw, ih, nw, pw, 1, pw);
    private static final VoxelShape BOUNDING_BOX_UP = Shapes.box(nw, 0, nw, pw, h, pw);
    private static final VoxelShape BOUNDING_BOX_SOUTH = Shapes.box(nw, nw, 0, pw, pw, h);
    private static final VoxelShape BOUNDING_BOX_NORTH = Shapes.box(nw, nw, ih, pw, pw, 1);
    private static final VoxelShape BOUNDING_BOX_EAST = Shapes.box(0, nw, nw, h, pw, pw);
    private static final VoxelShape BOUNDING_BOX_WEST = Shapes.box(ih, nw, nw, 1, pw, pw);

    static {
        BOUNDING_BOXES.put(Direction.DOWN, BOUNDING_BOX_DOWN);
        BOUNDING_BOXES.put(Direction.UP, BOUNDING_BOX_UP);
        BOUNDING_BOXES.put(Direction.SOUTH, BOUNDING_BOX_SOUTH);
        BOUNDING_BOXES.put(Direction.NORTH, BOUNDING_BOX_NORTH);
        BOUNDING_BOXES.put(Direction.EAST, BOUNDING_BOX_EAST);
        BOUNDING_BOXES.put(Direction.WEST, BOUNDING_BOX_WEST);
    }

    public BlockMarkerBase(String id, BlockBehaviour.Properties props) {
        super(
                id,
                props
                        .strength(0.25F)
                        .noOcclusion()
                        .noCollission()
        );

        this.registerDefaultState(this.getStateDefinition().any()
                        .setValue(BuildCraftProperties.BLOCK_FACING_6, Direction.UP)
//                .setValue(BuildCraftProperties.ACTIVE, false) // Calen: only changes, never used
        );
    }

    @Override
//    protected BlockStateContainer createBlockState()
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BuildCraftProperties.BLOCK_FACING_6);
//        builder.add(BuildCraftProperties.ACTIVE); // Calen: only changes, never used
    }

//    @Override
//    public int getMetaFromState(BlockState state)
//    {
//        return state.getValue(BuildCraftProperties.BLOCK_FACING_6).ordinal();
//    }

    //    @Override
//    public BlockState getStateFromMeta(int meta)
//    {
//        return defaultBlockState().setValue(BuildCraftProperties.BLOCK_FACING_6, Direction.values()[meta]);
//    }

    // Calen: ACTIVE only changes, but never used
//    @Override
////    public BlockState getActualState(BlockState state, LevelAccessor world, BlockPos pos)
//    public BlockState getActualState(BlockState state, LevelAccessor world, BlockPos pos, BlockEntity tile)
//    {
//        tile = tile == null ? world.getBlockEntity(pos) : tile;
//        if (tile instanceof TileMarker marker)
//        {
//            state = state.setValue(BuildCraftProperties.ACTIVE, marker.isActiveForRender());
//        }
//        return state;
//    }

    // Calen: 1.18.2 moved to FMLClientSetupEvent: ItemBlockRenderTypes.setRenderLayer(BCCoreBlocks.MARKER_VOLUME.get(), RenderType.cutout());
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public RenderType getBlockLayer() { return RenderType.cutout(); }

//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }

//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//         return false;
//    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
//    public AABB getCollisionShape(BlockState state, BlockAccess world, BlockPos pos)
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
//    public AABB getBoundingBox(BlockState state, BlockAccess source, BlockPos pos)
    public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext context) {
        return BOUNDING_BOXES.get(state.getValue(BuildCraftProperties.BLOCK_FACING_6));
    }

    @Override
//    public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, InteractionHand hand)
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockState state = defaultBlockState();
        state = state.setValue(BuildCraftProperties.BLOCK_FACING_6, facing);
        return state;
    }

    // TODO Calen canPlaceBlockOnSide??? canSurvive?
//    @Override
    public boolean canPlaceBlockOnSide(Level world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        return state.isFaceSturdy(world, pos.relative(side.getOpposite()), side);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_60514_) {
        if (state.getBlock() != this) {
            return;
        }
        Direction sideOn = state.getValue(BuildCraftProperties.BLOCK_FACING_6);
        if (!canPlaceBlockOnSide(world, pos, sideOn)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public InteractionResult attemptRotation(Level world, BlockPos pos, BlockState state, Direction sideWrenched) {
        if (state.getBlock() instanceof BlockMarkerBase) {// Just check to make sure we have the right core...
            Property<Direction> prop = BuildCraftProperties.BLOCK_FACING_6;
            return VanillaRotationHandlers.rotateEnumFacing(world, pos, state, prop, VanillaRotationHandlers.ROTATE_FACING);
        } else {
            return InteractionResult.PASS;
        }
    }
}
