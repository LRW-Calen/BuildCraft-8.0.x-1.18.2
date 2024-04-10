package buildcraft.builders;

import buildcraft.builders.container.*;
import buildcraft.builders.gui.*;
import buildcraft.builders.tile.*;
import buildcraft.lib.misc.GuiUtil;
import buildcraft.lib.misc.MessageUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class BCBuildersMenuTypes
{
    public static final MenuType<ContainerBuilder> BUILDER = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileBuilder tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerBuilder(BCBuildersMenuTypes.BUILDER, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );
    public static final MenuType<ContainerArchitectTable> ARCHITECT_TABLE = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileArchitectTable tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerArchitectTable(BCBuildersMenuTypes.ARCHITECT_TABLE, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );
    public static final MenuType<ContainerElectronicLibrary> ELECTRONIC_LIBRARY = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileElectronicLibrary tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerElectronicLibrary(BCBuildersMenuTypes.ELECTRONIC_LIBRARY, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );
    public static final MenuType<ContainerReplacer> REPLACER = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileReplacer tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerReplacer(BCBuildersMenuTypes.REPLACER, windowId, inv.player, tile);
                }
                else
                {
                    return null;
                }
            }
    );
    public static final MenuType<ContainerFiller> FILLER = IForgeMenuType.create((windowId, inv, data) ->
            {
                if (inv.player.level.getBlockEntity(data.readBlockPos()) instanceof TileFiller tile)
                {
                    MessageUtil.clientHandleUpdateTileMsgBeforeOpen(tile, data);
                    return new ContainerFiller(BCBuildersMenuTypes.FILLER, windowId, inv.player, tile);
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
                BUILDER.setRegistryName("builder"),
                ARCHITECT_TABLE.setRegistryName("architect_table"),
                ELECTRONIC_LIBRARY.setRegistryName("electronic_library"),
                REPLACER.setRegistryName("replacer"),
                FILLER.setRegistryName("filler")
        );

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
//        MenuScreens.register(BUILDER, BCBuildersScreenConstructors.BUILDER);
//        MenuScreens.register(ARCHITECT_TABLE, BCBuildersScreenConstructors.ARCHITECT_TABLE);
//        MenuScreens.register(ELECTRONIC_LIBRARY, BCBuildersScreenConstructors.ELECTRONIC_LIBRARY);
//        MenuScreens.register(REPLACER, BCBuildersScreenConstructors.REPLACER);
//        MenuScreens.register(FILLER, BCBuildersScreenConstructors.FILLER);
            MenuScreens.register(BUILDER, GuiBuilder::new);
            MenuScreens.register(ARCHITECT_TABLE, GuiArchitectTable::new);
            MenuScreens.register(ELECTRONIC_LIBRARY, GuiElectronicLibrary::new);
            MenuScreens.register(REPLACER, GuiReplacer::new);
            MenuScreens.register(FILLER, GuiFiller::new);
        }
    }
}
