package buildcraft.datagen.core;

import buildcraft.api.enums.EnumDecoratedBlock;
import buildcraft.core.BCCoreBlocks;
import buildcraft.core.BCCoreItems;
import buildcraft.core.BCCoreModBusEventDist;
import buildcraft.core.block.BlockDecoration;
import buildcraft.factory.BCFactoryItems;
import buildcraft.silicon.BCSiliconItems;
import buildcraft.silicon.BCSiliconModBusEventDist;
import buildcraft.transport.BCTransportItems;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class CoreItemModelProvider extends ItemModelProvider
{
    private static final ResourceLocation generated = new ResourceLocation("minecraft", "item/generated");

    public CoreItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        // Gears
        withExistingParent(BCCoreItems.gearDiamond.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/gear_diamond");
        withExistingParent(BCCoreItems.gearWood.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/gear_wood");
        withExistingParent(BCCoreItems.gearStone.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/gear_stone");
        withExistingParent(BCCoreItems.gearIron.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/gear_iron");
        withExistingParent(BCCoreItems.gearGold.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/gear_gold");
        // list
        ResourceLocation list = BCCoreItems.list.get().getRegistryName();
        getBuilder(list.toString())
                .override()
                .model(
                        withExistingParent(list.getNamespace() + ":item/" + list.getPath() + "/clean", generated)
                                .texture("layer0", "buildcraftcore:items/list/clean")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_USED, 0)
                .end()
                .override()
                .model(
                        withExistingParent(list.getNamespace() + ":item/" + list.getPath() + "/used", generated)
                                .texture("layer0", "buildcraftcore:items/list/used")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_USED, 1)
                .end()
        ;
        // mapLocation
        ResourceLocation mapLocation = BCCoreItems.mapLocation.get().getRegistryName();
        getBuilder(mapLocation.toString())
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/clean", generated)
                                .texture("layer0", "buildcraftcore:items/map/clean")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 0)
                .end()
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/spot", generated)
                                .texture("layer0", "buildcraftcore:items/map/spot")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 1)
                .end()
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/area", generated)
                                .texture("layer0", "buildcraftcore:items/map/area")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 2)
                .end()
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/path", generated)
                                .texture("layer0", "buildcraftcore:items/map/path")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 3)
                .end()
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/zone", generated)
                                .texture("layer0", "buildcraftcore:items/map/zone")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 4)
                .end()
                .override()
                .model(
                        withExistingParent(mapLocation.getNamespace() + ":item/" + mapLocation.getPath() + "/path_repeating", generated)
                                .texture("layer0", "buildcraftcore:items/map/path_repeating")
                )
                .predicate(BCCoreModBusEventDist.PREDICATE_MAP_TYPE, 5)
                .end()
        ;

        // wrench
        withExistingParent(BCCoreItems.wrench.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/handheld"))
                .texture("layer0", "buildcraftcore:items/wrench");

        // marker_connector
        withExistingParent(BCCoreItems.markerConnector.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/handheld"))
                .texture("layer0", "buildcraftcore:items/marker_connector");

        // goggles
        withExistingParent(BCCoreItems.goggles.get().getRegistryName().toString(), new ResourceLocation("minecraft:item/generated"))
                .texture("layer0", "buildcraftcore:items/goggles");

        // fragile fluid shard
        withExistingParent(
                BCCoreItems.fragileFluidShard.get().getRegistryName().toString(),
//                new ResourceLocation("forge", "item/bucket")
                new ResourceLocation("forge", "item/bucket")
        )
                .texture("base", "buildcraftcore:items/fragile_fluid_shard_base")
                .texture("fluid", "buildcraftcore:items/fragile_fluid_shard_fluid")
                .customLoader(DynamicBucketModelBuilder::begin)
                .fluid(Fluids.WATER.getSource())
        ;
        // spring
        withExistingParent(BCCoreBlocks.springOil.get().getRegistryName().toString(), "minecraft:block/bedrock");
        withExistingParent(BCCoreBlocks.springWater.get().getRegistryName().toString(), "minecraft:block/bedrock");
        // markerVolume
        withExistingParent(BCCoreBlocks.markerVolume.get().getRegistryName().toString(), generated)
                .texture("layer0", "buildcraftcore:items/marker_volume")
        ;
        // markerPath
        withExistingParent(BCCoreBlocks.markerPath.get().getRegistryName().toString(), generated)
                .texture("layer0", "buildcraftcore:items/marker_path")
        ;

        // engine
//        withExistingParent(BCCoreBlocks.ENGINE_WOOD.get().getRegistryName().toString(), new ResourceLocation("minecraft:builtin/entity"));
//        withExistingParent(BCCoreBlocks.ENGINE_CREATIVE.get().getRegistryName().toString(), new ResourceLocation("minecraft:builtin/entity"));
        getBuilder(BCCoreBlocks.engineWood.get().getRegistryName().toString()).parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));
        getBuilder(BCCoreBlocks.engineCreative.get().getRegistryName().toString()).parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));
        BCCoreItems.colourBrushMap.forEach((colour, item) ->
        {
            withExistingParent(item.get().getRegistryName().toString(), new ResourceLocation("minecraft", "item/handheld")).texture("layer0", "buildcraftcore:items/paintbrush/" + (colour == null ? "clean" : colour.getName()));
        });

        // decorated
        BCCoreBlocks.decoratedMap.values().forEach(decorated ->
                {
                    BlockDecoration block = decorated.get();
                    ResourceLocation rl = block.getRegistryName();
                    EnumDecoratedBlock type = block.DECORATED_TYPE;
                    withExistingParent(rl.toString(), rl.getNamespace() + ":block/decorated/" + type.getSerializedName());

                }
        );

        // volumeBox
        withExistingParent(BCCoreItems.volumeBox.get().getRegistryName().toString(), new ResourceLocation("minecraft:block/block"))
                .element()
                .from(2, 0, 0).to(14, 2, 2)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(0, 0, 2).to(2, 2, 14)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(2, 0, 14).to(14, 2, 16)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(14, 0, 2).to(16, 2, 14)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(2, 14, 0).to(14, 16, 2)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(0, 14, 2).to(2, 16, 14)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(2, 14, 14).to(14, 16, 16)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(14, 14, 2).to(16, 16, 14)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(0, 2, 0).to(2, 14, 2)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(14, 2, 0).to(16, 14, 2)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(14, 2, 14).to(16, 14, 16)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .element()
                .from(0, 2, 14).to(2, 14, 16)
                .face(Direction.DOWN).texture("#main").end()
                .face(Direction.UP).texture("#main").end()
                .face(Direction.NORTH).texture("#main").end()
                .face(Direction.SOUTH).texture("#main").end()
                .face(Direction.WEST).texture("#main").end()
                .face(Direction.EAST).texture("#main").end()
                .end()
                .texture("main", "buildcraftcore:lasers/marker_volume_connected")
        ;

//        ResourceLocation paintbrush = BCCoreItems.paintbrush.get().getRegistryName();
//        ResourceLocation handheld = new ResourceLocation("minecraft", "item/handheld");
//        withExistingParent(paintbrush.getNamespace() + ":item/paintbrush", handheld)
//                .texture("layer0", "buildcraftcore:items/paintbrush/clean")
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/white", handheld).texture("layer0", "buildcraftcore:items/paintbrush/white"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.WHITE.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/orange", handheld).texture("layer0", "buildcraftcore:items/paintbrush/orange"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.ORANGE.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/magenta", handheld).texture("layer0", "buildcraftcore:items/paintbrush/magenta"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.MAGENTA.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/lightblue", handheld).texture("layer0", "buildcraftcore:items/paintbrush/lightblue"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.LIGHT_BLUE.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/yellow", handheld).texture("layer0", "buildcraftcore:items/paintbrush/yellow"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.YELLOW.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/lime", handheld).texture("layer0", "buildcraftcore:items/paintbrush/lime"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.LIME.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/pink", handheld).texture("layer0", "buildcraftcore:items/paintbrush/pink"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.PINK.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/silver", handheld).texture("layer0", "buildcraftcore:items/paintbrush/silver"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.LIGHT_GRAY.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/cyan", handheld).texture("layer0", "buildcraftcore:items/paintbrush/cyan"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.CYAN.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/purple", handheld).texture("layer0", "buildcraftcore:items/paintbrush/purple"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.PURPLE.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/blue", handheld).texture("layer0", "buildcraftcore:items/paintbrush/blue"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.BLUE.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/brown", handheld).texture("layer0", "buildcraftcore:items/paintbrush/brown"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.BROWN.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/green", handheld).texture("layer0", "buildcraftcore:items/paintbrush/green"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.GREEN.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/red", handheld).texture("layer0", "buildcraftcore:items/paintbrush/red"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.RED.getId())
//                .end()
//                .override()
//                .model(withExistingParent(paintbrush.getNamespace() + ":item/paintbrush/black", handheld).texture("layer0", "buildcraftcore:items/paintbrush/black"))
//                .predicate(new ResourceLocation("buildcraft", "colour"), DyeColor.BLACK.getId())
//                .end()
//        ;
        with16Colours(BCTransportItems.wire.get());
    }

    private void with16Colours(Item item)
    {
        ResourceLocation reg = item.getRegistryName();
        ResourceLocation handheld = new ResourceLocation("minecraft", "item/handheld");

        ItemModelBuilder model = withExistingParent(reg.getNamespace() + ":item/" + reg.getPath(), handheld);
        for (DyeColor colour : DyeColor.values())
        {
            model = model
                    .override()
                    .model(withExistingParent(reg.getNamespace() + ":item/" + reg.getPath() + "/" + colour.getName().toLowerCase(), handheld).texture("layer0", reg.getNamespace() + ":items/" + reg.getPath() + "/" + colour.getName().toLowerCase()))
                    .predicate(new ResourceLocation("buildcraft", "colour"), colour.getId())
                    .end();
        }
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Item model provider: " + modid;
    }

    public boolean textureExists(ResourceLocation texture)
    {
        return existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures");
    }
}