/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.lib;

import buildcraft.api.BCModules;
import buildcraft.api.core.BCLog;
import buildcraft.lib.block.VanillaRotationHandlers;
import buildcraft.lib.chunkload.ChunkLoaderManager;
import buildcraft.lib.expression.ExpressionDebugManager;
import buildcraft.lib.list.VanillaListHandlers;
import buildcraft.lib.marker.MarkerCache;
import buildcraft.lib.misc.CapUtil;
import buildcraft.lib.misc.ExpressionCompat;
import buildcraft.lib.net.MessageManager;
import buildcraft.lib.net.cache.BuildCraftObjectCaches;
import buildcraft.lib.registry.MigrationManager;
import buildcraft.lib.script.ReloadableRegistryManager;
import buildcraft.lib.registry.TagManager;
import buildcraft.lib.registry.TagManager.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

//@formatter:off
@Mod(BCLib.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//@formatter:on
public class BCLib
{
    public static final String MODID = "buildcraftlib";
    public static final String VERSION = "$version";
    public static final String MC_VERSION = "${mcversion}";
    public static final String GIT_BRANCH = "${git_branch}";
    public static final String GIT_COMMIT_HASH = "${git_commit_hash}";
    public static final String GIT_COMMIT_MSG = "${git_commit_msg}";
    public static final String GIT_COMMIT_AUTHOR = "${git_commit_author}";

    public static final boolean DEV = VERSION.startsWith("$") || Boolean.getBoolean("buildcraft.dev");

    public static BCLib INSTANCE;

    public static ModContainer MOD_CONTAINER;

    static
    {
        BCLibRegistries.fmlPreInit();
    }

    public BCLib()
    {
        INSTANCE = this;
        BCLibItems.enableGuide();
        BCLibItems.enableDebugger();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        BCLibItems.ITEMS.register(modEventBus);

        modEventBus.addListener(CapUtil::registerCaps);

        // various sprite registers
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            BCLibSprites.fmlPreInitClient();
        }
        modEventBus.addGenericListener(MenuType.class, BCLibMenuTypes::registerAll);

        ExpressionCompat.setup();
    }

    // Calen: recipe type registry is frozen at FMLConstructModEvent
    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event)
    {
        BCLibRegistries.initRecipeRegistry();
    }

    @SubscribeEvent
    public static void preInit(FMLConstructModEvent evt)
    {
        BCLibItems.fmlPreInit();

        MOD_CONTAINER = ModList.get().getModContainerById(MODID).get();

        try
        {
            BCLog.logger.info("");
        }
        catch (NoSuchFieldError e)
        {
            throw throwBadClass(e, BCLog.class);
        }
        BCLog.logger.info("Starting BuildCraft " + BCLib.VERSION);
        BCLog.logger.info("Copyright (c) the BuildCraft team, 2011-2018");
        BCLog.logger.info("https://www.mod-buildcraft.com");
        if (!GIT_COMMIT_HASH.startsWith("${"))
        {
            BCLog.logger.info("Detailed Build Information:");
            BCLog.logger.info("  Branch " + GIT_BRANCH);
            BCLog.logger.info("  Commit " + GIT_COMMIT_HASH);
            BCLog.logger.info("    " + GIT_COMMIT_MSG);
            BCLog.logger.info("    committed by " + GIT_COMMIT_AUTHOR);
        }
        BCLog.logger.info("");
        BCLog.logger.info("Loaded Modules:");
        for (BCModules module : BCModules.VALUES)
        {
            if (module.isLoaded())
            {
                BCLog.logger.info("  - " + module.lowerCaseName);
            }
        }
        BCLog.logger.info("Missing Modules:");
        for (BCModules module : BCModules.VALUES)
        {
            if (!module.isLoaded())
            {
                BCLog.logger.info("  - " + module.lowerCaseName);
            }
        }
        BCLog.logger.info("");

        ExpressionDebugManager.logger = BCLog.logger::info;
//        ExpressionCompat.setup(); // Calen: moved to <init> to be loaded early enough, or the Silicon/Transport/Factory model classed will cause Exception when running <cinit>

//        BCLibRegistries.fmlPreInit(); // Calen: moved to static
        BCLibProxy.getProxy().fmlPreInit();
//        BCLibItems.fmlPreInit();
//
        BuildCraftObjectCaches.fmlPreInit();
//        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, BCLibProxy.getProxy());
//
        MinecraftForge.EVENT_BUS.register(BCLibEventDistForgeBus.class);
        MinecraftForge.EVENT_BUS.register(MigrationManager.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(FluidManager.class); // Calen: not used in 1.18.2
//
//        // Set max chunk limit for quarries: 1 chunk for quarry itself and 5 * 5 chunks square for working area
//        ForgeChunkManager.getConfig().get(MODID, "maximumChunksPerTicket", 26);
//        ForgeChunkManager.syncConfigDefaults();
//        ForgeChunkManager.setForcedChunkLoadingCallback(BCLib.INSTANCE, ChunkLoaderManager::rebindTickets);
        ForgeChunkManager.setForcedChunkLoadingCallback(BCLib.MODID, ChunkLoaderManager::rebindTickets);
    }

    public static Error throwBadClass(Error e, Class<?> cls) throws Error
    {
        throw new Error(
                "Bad " + cls + " loaded from " + cls.getClassLoader() + " domain: " + cls.getProtectionDomain(), e
        );
    }

    // Calen: moved to BCLibEventDistForgeBus#serverStarting because it is Forge Bus Event in 1.18.2
//    @Mod.EventHandler
//    public static void serverStarting(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandBuildCraft());
//    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent evt)
    {
        BCLibProxy.getProxy().fmlInit();
//
//        BCLibRegistries.fmlInit();
        VanillaListHandlers.fmlInit();
//        VanillaPaintHandlers.fmlInit();
        VanillaRotationHandlers.fmlInit();

//        RegistrationHelper.registerOredictEntries();
    }

    @SubscribeEvent
    public static void postInit(FMLLoadCompleteEvent evt)
    {
        ReloadableRegistryManager.loadAll();
        BCLibProxy.getProxy().fmlPostInit();
        BuildCraftObjectCaches.fmlPostInit();
        VanillaListHandlers.fmlPostInit();
        MarkerCache.postInit();
        MessageManager.fmlPostInit();
    }

    private static final TagManager tagManager = new TagManager();

    static
    {
        startBatch();
        registerTag("item.guide").reg("guide").locale("buildcraft.guide")
//                .model("guide")
//                .tab("vanilla.misc")
                .tab("buildcraft.main")
        ;
        registerTag("item.guide.note").reg("guide_note").locale("buildcraft.guide_note")
//                .model("guide_note")
//                .tab("vanilla.misc")
                .tab("buildcraft.main")
        ;
        registerTag("item.debugger").reg("debugger").locale("debugger")
//                .model("debugger")
//                .tab("vanilla.misc")
                .tab("buildcraft.main")
        ;
//        endBatch(TagManager.prependTags("buildcraftlib:", TagManager.EnumTagType.REGISTRY_NAME, TagManager.EnumTagType.MODEL_LOCATION));
        endBatch(TagManager.prependTags("buildcraftlib:", TagManager.EnumTagType.REGISTRY_NAME));
    }

    private static TagEntry registerTag(String id)
    {
//        return TagManager.registerTag(id);
//        return TagManager.registerTag(id);
        return tagManager.registerTag(id);
    }

    private static void startBatch()
    {
//        TagManager.startBatch();
        tagManager.startBatch();
    }

    private static void endBatch(Consumer<TagEntry> consumer)
    {
//        TagManager.endBatch(consumer);
        tagManager.endBatch(consumer);
    }
}