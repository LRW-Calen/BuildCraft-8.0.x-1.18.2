/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.factory.block;

import buildcraft.factory.BCFactoryBlocks;
import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.factory.tile.TilePump;
import buildcraft.lib.block.IBlockWithTickableTE;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockPump<T extends TilePump> extends BlockBCTile_Neptune<TilePump> implements IBlockWithTickableTE<TilePump>
{
    public BlockPump(String idBC, Properties props)
    {
        super(idBC, props);
    }

    @Override
    public TileBC_Neptune newBlockEntity(BlockPos pos, BlockState state)
    {
        return BCFactoryBlocks.pumpTile.get().create(pos, state);
    }

//    @Override
//    @Nullable
//    public BlockEntityTicker<T> getTicker(BlockState pState, BlockEntityType pBlockEntityType)
//    {
////        return pBlockEntityType==BCFactoryBlockEntities.PUMP.get()?(BlockEntityTicker<T>)TilePump::tick:null;
//        return BCCoreBlockEntities.createTickerHelper(pBlockEntityType, BCFactoryBlocks.pumpTile.get(), TilePump::tick);
//    }
}