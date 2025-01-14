/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.transport;

import buildcraft.api.core.EnumHandlerPriority;
import buildcraft.api.transport.pipe.EnumPipeColourType;
import buildcraft.api.transport.pipe.PipeApi;
import buildcraft.api.transport.pipe.PipeConnectionAPI;
import buildcraft.api.transport.pipe.PipeFlowType;
import buildcraft.transport.pipe.PipeRegistry;
import buildcraft.transport.pipe.StripesRegistry;
import buildcraft.transport.pipe.flow.PipeFlowFluids;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import buildcraft.transport.pipe.flow.PipeFlowPower;
import buildcraft.transport.pipe.flow.PipeFlowStructure;
import buildcraft.transport.stripes.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;

public class BCTransportRegistries {

    public static void preInit() {
        PipeApi.pipeRegistry = PipeRegistry.INSTANCE;
        PipeApi.stripeRegistry = StripesRegistry.INSTANCE;
        PipeApi.extensionManager = PipeExtensionManager.INSTANCE;
        MinecraftForge.EVENT_BUS.register(PipeExtensionManager.INSTANCE);

        PipeApi.flowItems = new PipeFlowType(PipeFlowItems::new, PipeFlowItems::new);
        PipeApi.flowFluids = new PipeFlowType(PipeFlowFluids::new, PipeFlowFluids::new);
        PipeApi.flowPower = new PipeFlowType(PipeFlowPower::new, PipeFlowPower::new);
        PipeApi.flowStructure = new PipeFlowType(PipeFlowStructure::new, PipeFlowStructure::new);
        PipeApi.flowStructure.fallbackColourType = EnumPipeColourType.BORDER_OUTER;
    }

    public static void init() {
        PipeConnectionAPI.registerConnection(Blocks.BREWING_STAND,
                (world, pos, face, state) -> face.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? 4 / 16F : 0);

        // Item use stripes handlers
        PipeApi.stripeRegistry.addHandler(StripesHandlerPlant.INSTANCE);
        PipeApi.stripeRegistry.addHandler(StripesHandlerShears.INSTANCE);
        PipeApi.stripeRegistry.addHandler(new StripesHandlerPipes());
        // PipeApi.stripeRegistry.addHandler(new StripesHandlerPipeWires());
        PipeApi.stripeRegistry.addHandler(StripesHandlerEntityInteract.INSTANCE, EnumHandlerPriority.LOW);
        PipeApi.stripeRegistry.addHandler(StripesHandlerHoe.INSTANCE);
        // PipeApi.stripeRegistry.addHandler(new StripesHandlerRightClick(), EnumHandlerPriority.LOW);
        PipeApi.stripeRegistry.addHandler(StripesHandlerDispenser.INSTANCE, EnumHandlerPriority.LOW);
        PipeApi.stripeRegistry.addHandler(StripesHandlerPlaceBlock.INSTANCE, EnumHandlerPriority.LOW);
        PipeApi.stripeRegistry.addHandler(StripesHandlerUse.INSTANCE, EnumHandlerPriority.LOW);

        // For testing
        // StripesHandlerDispenser.ITEM_CLASSES.add(ItemBucket.class);
        // StripesHandlerDispenser.ITEM_CLASSES.add(ItemMinecart.class);

        // StripesHandlerRightClick.items.add(Items.EGG);
        // StripesHandlerRightClick.items.add(Items.SNOWBALL);
        // StripesHandlerRightClick.items.add(Items.EXPERIENCE_BOTTLE);

        // Block breaking stripes handlers
        PipeApi.stripeRegistry.addHandler(StripesHandlerMinecartDestroy.INSTANCE);

        PipeApi.extensionManager.registerRetractionPipe(BCTransportPipes.voidItem);
    }
}
