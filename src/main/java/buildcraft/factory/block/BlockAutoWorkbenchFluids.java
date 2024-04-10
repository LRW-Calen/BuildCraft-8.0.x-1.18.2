/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.factory.block;

import buildcraft.factory.BCFactoryGuis;
import buildcraft.factory.tile.TileAutoWorkbenchFluids;
import buildcraft.factory.tile.TileAutoWorkbenchItems;
import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.lib.misc.GuiUtil;
import buildcraft.lib.misc.MessageUtil;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockAutoWorkbenchFluids extends BlockBCTile_Neptune
{

    public BlockAutoWorkbenchFluids(String idBC, BlockBehaviour.Properties props)
    {
        super(idBC, props);
    }

    @Override
//    public TileBC_Neptune createTileEntity(World world, IBlockState state)
    public TileBC_Neptune newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TileAutoWorkbenchFluids(pos, state);
    }

    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ)
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!world.isClientSide)
        {
            // TODO Calen tile impl MenuProvider
//            BCFactoryGuis.AUTO_WORKBENCH_FLUIDS.openGUI(player, pos);
            // Calen
            if (world.getBlockEntity(pos) instanceof TileAutoWorkbenchFluids tile)
            {
                MessageUtil.serverOpenTileGUI(player, tile);
            }
        }
//        return true;
        return InteractionResult.SUCCESS;
    }
}