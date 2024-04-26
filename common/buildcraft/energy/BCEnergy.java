package buildcraft.energy;

import buildcraft.api.enums.EnumSpring;
import buildcraft.core.BCCore;
import buildcraft.energy.recipe.CoolantRecipeSerializer;
import buildcraft.energy.recipe.FuelRecipeSerializer;
import buildcraft.lib.fluid.BCFluid;
import buildcraft.lib.registry.RegistryConfig;
import buildcraft.lib.registry.TagManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

//@formatter:off
//@Mod(
//        modid = BCEnergy.MODID,
//        name = "BuildCraft Energy",
//        version = BCLib.VERSION,
//        dependencies = "required-after:buildcraftcore@[" + BCLib.VERSION + "]"
//)
//@formatter:on
@Mod(BCEnergy.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCEnergy {
    public static final String MODID = "buildcraftenergy";

//    static {
//        FluidRegistry.enableUniversalBucket();
//    }

    // @Mod.Instance(MODID)
    public static BCEnergy INSTANCE;

    public BCEnergy() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public static void preInit(FMLConstructModEvent event) {
        RegistryConfig.useOtherModConfigFor(MODID, BCCore.MODID);
        BCEnergyConfig.preInit();
        BCEnergyEntities.preInit();
        BCEnergyWorldGen.preInit();
        // Calen: BCEnergyProxy.getProxy().fmlPreInit() Should before BCEnergyFluids.preInit() and BCEnergyBlocks.preInit() to set christmas special fluid data
        BCEnergyProxy.getProxy().fmlPreInit();

        BCEnergyFluids.preInit();
        BCEnergyBlocks.preInit();
        BCEnergyItems.preInit();

//        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, BCEnergyProxy.getProxy());
    }

    @SubscribeEvent
//    public static void init(FMLInitializationEvent evt)
    public static void init(FMLCommonSetupEvent event) {
        BCEnergyFluids.registerBucketDispenserBehavior();
//        BCEnergyRecipes.init(); // 1.18.2: use datagen
//        BCEnergyWorldGen.init(); // 1.18.2: moved to #preInit
        BCEnergyProxy.getProxy().fmlInit();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {
        // Calen: from BCFluidBlock#<init>
        for (RegistryObject<BCFluid.Source> fluid : BCEnergyFluids.allStill) {
            ItemBlockRenderTypes.setRenderLayer(fluid.get().getSource(), RenderType.solid());
            ItemBlockRenderTypes.setRenderLayer(fluid.get().getFlowing(), RenderType.solid());
        }
    }

    @SubscribeEvent
    public static void postInit(FMLLoadCompleteEvent event) {
        BCEnergyProxy.getProxy().fmlPostInit();
        BCEnergyConfig.validateBiomeNames();
//        registerMigrations();
        EnumSpring.OIL.liquidBlock = BCEnergyFluids.crudeOil[0].get().defaultFluidState().createLegacyBlock();
    }

    @SubscribeEvent
    public static void registerGui(RegistryEvent.Register<MenuType<?>> event) {
        BCEnergyMenuTypes.registerAll(event);
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        IForgeRegistry<RecipeSerializer<?>> registry = event.getRegistry();
        registry.register(FuelRecipeSerializer.INSTANCE);
        registry.register(CoolantRecipeSerializer.INSTANCE);
    }

    // TODO Calen biome???
//    @SubscribeEvent
//    public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> evt) {
////        Registry.register(Registry.BIOME_SOURCE, ">>>", BCBiomeProvider.TF_CODEC);
////        Registry.register(Registry.CHUNK_GENERATOR, ">>>", BCChunkGenerator.CODEC);
//    }

    private static final TagManager tagManager = new TagManager();

    static {
        // Calen: in namespace Energy
        startBatch();
        // Items
//        registerTag("item.glob.oil").reg("glob_of_oil").oldReg("glob_oil").locale("globOil").model("glob_oil");
//        registerTag("item.glob_oil").reg("glob_oil").locale("globOil").model("glob_oil");
        registerTag("item.glob_oil").reg("glob_oil").locale("globOil");
//        registerTag("item.oil_placer").reg("oil_placer").locale("oilPlacer").model("glob_oil");
        registerTag("item.oil_placer").reg("oil_placer").locale("oilPlacer");

        // Item Blocks

//        endBatch(TagManager.prependTags("buildcraftenergy:", TagManager.EnumTagType.REGISTRY_NAME, TagManager.EnumTagType.MODEL_LOCATION)
        endBatch(TagManager.prependTags("buildcraftenergy:", TagManager.EnumTagType.REGISTRY_NAME)
                .andThen(TagManager.setTab("buildcraft.main"))
        );

        // Calen: in namespace Core
        startBatch();
        // Item Blocks
//        registerTag("item.block.engine.bc.stone").reg("engine_stone").locale("engineStone").model("");
        registerTag("item.block.engine.bc.stone").reg("engine_stone").locale("engineStone");
//        registerTag("item.block.engine.bc.iron").reg("engine_iron").locale("engineIron").model("");
        registerTag("item.block.engine.bc.iron").reg("engine_iron").locale("engineIron");
        // Blocks
        registerTag("block.engine.bc.stone").reg("engine_stone").locale("engineStone");
//        registerTag("block.engine.bc.stone").locale("engine_stone");
        registerTag("block.engine.bc.iron").reg("engine_iron").locale("engineIron");
//        registerTag("block.engine.bc.iron").locale("engine_iron");

        // Tiles
        registerTag("tile.engine.stone").reg("engine_stone");
        registerTag("tile.engine.iron").reg("engine_iron");
        registerTag("tile.spring.oil").reg("spring_oil");

//        endBatch(TagManager.prependTags("buildcraftcore:", TagManager.EnumTagType.REGISTRY_NAME, TagManager.EnumTagType.MODEL_LOCATION)
        endBatch(TagManager.prependTags("buildcraftcore:", TagManager.EnumTagType.REGISTRY_NAME)
                .andThen(TagManager.setTab("buildcraft.main"))
        );
    }

    private static TagManager.TagEntry registerTag(String id) {
//        return TagManager.registerTag(id);
        return tagManager.registerTag(id);
    }

    private static void startBatch() {
//        TagManager.startBatch();
        tagManager.startBatch();
    }

    private static void endBatch(Consumer<TagManager.TagEntry> consumer) {
//        TagManager.endBatch(consumer);
        tagManager.endBatch(consumer);
    }
}
