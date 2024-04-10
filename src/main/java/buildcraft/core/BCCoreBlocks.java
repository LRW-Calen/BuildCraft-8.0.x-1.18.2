package buildcraft.core;

import buildcraft.api.enums.EnumDecoratedBlock;
import buildcraft.api.enums.EnumEngineType;
import buildcraft.api.enums.EnumSpring;
import buildcraft.core.block.*;
import buildcraft.core.item.ItemBlockDecorated;
import buildcraft.core.tile.TileMarkerPath;
import buildcraft.factory.tile.TileEngineCreative;
import buildcraft.factory.tile.TileEngineRedstone_BC8;
import buildcraft.core.tile.TileMarkerVolume;
import buildcraft.core.item.ItemBlockSpring;
import buildcraft.core.item.ItemEngine_BC8;
import buildcraft.lib.BCLib;
import buildcraft.lib.block.BlockPropertiesCreater;
import buildcraft.lib.engine.BlockEngineBase_BC8;
import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.registry.RegistrationHelper;
import buildcraft.lib.registry.RegistryConfig;
import buildcraft.lib.registry.TagManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public class BCCoreBlocks
{
    public static final RegistrationHelper HELPER = new RegistrationHelper(BCCore.MOD_ID);

    public static final Map<EnumEngineType, BiFunction<BlockPos, BlockState, ? extends TileEngineBase_BC8>> engineTileConstructors = new EnumMap(EnumEngineType.class);
    public static final Map<EnumEngineType, RegistryObject<? extends BlockEngineBase_BC8>> engineBlockMap = new EnumMap(EnumEngineType.class);
    public static final Map<EnumDecoratedBlock, RegistryObject<BlockDecoration>> decoratedMap = new HashMap<>();

    public static RegistryObject<BlockEngine_BC8> engineWood;
    public static RegistryObject<BlockEngine_BC8> engineCreative;
    public static RegistryObject<BlockSpring> springWater;
    public static RegistryObject<BlockSpring> springOil;
//    public static RegistryObject<BlockDecoration> decorated;
    public static RegistryObject<BlockMarkerVolume> markerVolume;
    public static RegistryObject<BlockMarkerPath> markerPath;
//    public static BlockPowerConsumerTester powerTester;

    public static RegistryObject<BlockEntityType<TileEngineRedstone_BC8>> engineWoodTile;
    public static RegistryObject<BlockEntityType<TileEngineCreative>> engineCreativeTile;
    public static RegistryObject<BlockEntityType<TileMarkerVolume>> markerVolumeTile;
    public static RegistryObject<BlockEntityType<TileMarkerPath>> markerPathTile;
//    public static RegistryObject<BlockEntityType<TilePowerConsumerTester>> powerTesterTile;
//    public static RegistryObject<BlockEntityType<TileSpringOil>> springOiltILE;

    private static final BlockBehaviour.Properties SPRING_PROPERTIES =
            BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.0F)
                    .noDrops()
                    .sound(SoundType.STONE)
                    .randomTicks() // 需要随机tick
            ;

    // Calen: static initialize for energy module access
    static
    {
        springWater = HELPER.addBlockAndItem(
                "block.spring.water",
                SPRING_PROPERTIES,
                (idBC, properties) -> new BlockSpring(idBC, properties, EnumSpring.WATER),
                ItemBlockSpring::new
        );
        springOil = HELPER.addBlockAndItem(
                "block.spring.oil",
                SPRING_PROPERTIES,
                (idBC, properties) -> new BlockSpring(idBC, properties, EnumSpring.OIL),
                ItemBlockSpring::new
        );
        String registryIdDecorated = TagManager.getTag("block.decorated", TagManager.EnumTagType.REGISTRY_NAME).replace(BCCore.MOD_ID + ":", "");
        for (EnumDecoratedBlock decoratedBlock : EnumDecoratedBlock.values())
        {
            RegistryObject<BlockDecoration> reg = HELPER.addBlockAndItem(
                    "block.decorated",
                    registryIdDecorated + "_" + decoratedBlock.getSerializedName(),
                    BlockPropertiesCreater.createDefaultProperties(Material.METAL),
                    (idBC, prop) -> new BlockDecoration(idBC, prop, decoratedBlock),
                    (idBC, prop) -> new ItemBlockDecorated(idBC, prop, decoratedBlock)
            );
            decoratedMap.put(decoratedBlock, reg);
        }
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.DESTROY), ItemBlockDecorated::new);
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.BLUEPRINT), ItemBlockDecorated::new);
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.LEATHER), ItemBlockDecorated::new);
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.LASER_BACK), ItemBlockDecorated::new);
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.PAPER), ItemBlockDecorated::new);
//        decorated = HELPER.addBlockAndItem("block.decorated", BlockPropertiesCreater.createDefaultProperties(Material.METAL), (idBC, prop)->new BlockDecoration(idBC,prop, EnumDecoratedBlock.TEMPLATE), ItemBlockDecorated::new);
        markerVolume = HELPER.addBlockAndItem("block.marker.volume", BlockPropertiesCreater.createDefaultProperties(Material.DECORATION).strength(0.25F), BlockMarkerVolume::new);
        markerPath = HELPER.addBlockAndItem("block.marker.path", BlockPropertiesCreater.createDefaultProperties(Material.DECORATION), BlockMarkerPath::new);
        if (BCLib.DEV)
        {
//            powerTester = HELPER.addBlockAndItem(new BlockPowerConsumerTester(Material.IRON,"block.power_tester"));
        }

        engineWood = registerEngine(EnumEngineType.WOOD, TileEngineRedstone_BC8::new);
        engineCreative = registerEngine(EnumEngineType.CREATIVE, TileEngineCreative::new);

        markerVolumeTile = HELPER.registerTile("tile.marker.volume", TileMarkerVolume::new, markerVolume);
        markerPathTile = HELPER.registerTile("tile.marker.path", TileMarkerPath::new, markerPath);
        engineWoodTile = HELPER.registerTile("tile.engine.wood", TileEngineRedstone_BC8::new, engineWood);
        engineCreativeTile = HELPER.registerTile("tile.engine.creative", TileEngineCreative::new, engineCreative);
        if (BCLib.DEV)
        {
//            HELPER.registerTile(TilePowerConsumerTester.class, "tile.power_tester");
        }
    }

    public static void init()
    {

    }

    private static final BlockBehaviour.Properties ENGINE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL)
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
            .noOcclusion();

    public static RegistryObject<BlockEngine_BC8> registerEngine(EnumEngineType type, BiFunction<BlockPos, BlockState, ? extends TileEngineBase_BC8> constructor)
    {
        RegistryObject<BlockEngine_BC8> engine = null;
        String regName = TagManager.getTag("block.engine.bc." + type.unlocalizedTag, TagManager.EnumTagType.REGISTRY_NAME).replace(BCCore.MOD_ID + ":", "");
        if (RegistryConfig.isEnabled(
                "engines",
                type.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT),
                TagManager.getTag("block.engine.bc." + type.unlocalizedTag, TagManager.EnumTagType.UNLOCALIZED_NAME)
        ))
        {
            String id = "block.engine.bc." + type.unlocalizedTag;
//            engine = HELPER.addBlockAndItem(id, ENGINE_PROPERTIES, (idBC, properties) -> new BlockEngine_BC8(idBC, properties, type), ItemEngine_BC8::new);
            engine = HELPER.addBlockAndItem(id,
                    BlockBehaviour.Properties.of(Material.METAL)
                            .strength(5.0F, 10.0F)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                    , (idBC, properties) -> new BlockEngine_BC8(idBC, properties, type), ItemEngine_BC8::new);
            engineTileConstructors.put(type, constructor);
            engineBlockMap.put(type, engine);
        }
        return engine;
    }


}