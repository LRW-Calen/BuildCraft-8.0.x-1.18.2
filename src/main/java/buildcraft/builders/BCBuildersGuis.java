/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.builders;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkHooks;

public enum BCBuildersGuis
{
    ARCHITECT,
    BUILDER,
    FILLER,
    LIBRARY,
    REPLACER,
    FILLER_PLANNER;

    public void openGUI(Player player)
    {
//        player.openGui(BCBuilders.INSTANCE, ordinal(), player.getEntityWorld(), 0, 0, 0);
        openGUI(player, BlockPos.ZERO);
    }

    public void openGUI(Player player, BlockPos pos)
    {
//        player.openGui(BCBuilders.INSTANCE, ordinal(), player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
        if (player instanceof ServerPlayer serverPlayer)
        {
//            player.openMenu(state.getMenuProvider(player.level, pos));
            if (serverPlayer.level.getBlockEntity(pos) instanceof MenuProvider menuProvider)
            {
                NetworkHooks.openGui(serverPlayer, menuProvider, pos);
            }
            else
            {
                player.sendMessage(new TranslatableComponent("buildcraft.error.open_null_menu"), Util.NIL_UUID);
            }
        }
    }
}