package buildcraft.robotics;

import buildcraft.energy.BCEnergyMenuTypes;
import buildcraft.energy.client.gui.GuiEngineIron_BC8;
import buildcraft.energy.client.gui.GuiEngineStone_BC8;
import buildcraft.energy.container.ContainerEngineStone_BC8;
import buildcraft.energy.tile.TileEngineStone_BC8;
import buildcraft.lib.misc.MessageUtil;
import buildcraft.robotics.container.ContainerProgrammingTable_Neptune;
import buildcraft.robotics.container.ContainerZonePlanner;
import buildcraft.robotics.gui.GuiProgrammingTable_Neptune;
import buildcraft.robotics.gui.GuiZonePlanner;
import buildcraft.robotics.tile.TileZonePlanner;
import buildcraft.silicon.tile.TileProgrammingTable_Neptune;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class BCRoboticsMenuTypes
{
    public static final MenuType<ContainerZonePlanner> ZONE_PLANNER = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileZonePlanner tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerZonePlanner(BCRoboticsMenuTypes.ZONE_PLANNER, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );
    public static final MenuType<ContainerProgrammingTable_Neptune> PROGRAMMING_TABLE = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileProgrammingTable_Neptune tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerProgrammingTable_Neptune(BCRoboticsMenuTypes.PROGRAMMING_TABLE, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );

    public static void registerAll(RegistryEvent.Register<MenuType<?>> event)
    {
        event.getRegistry().registerAll(
                ZONE_PLANNER.setRegistryName("zone_planner"),
                PROGRAMMING_TABLE.setRegistryName("programming_table")
        );

//        MenuScreens.register(ENGINE_IRON, BCEnergyScreenConstructors.ENGINE_IRON);
//        MenuScreens.register(ENGINE_STONE, BCEnergyScreenConstructors.ENGINE_STONE);
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            MenuScreens.register(ZONE_PLANNER, GuiZonePlanner::new);
            MenuScreens.register(PROGRAMMING_TABLE, GuiProgrammingTable_Neptune::new);
        }
    }
}