/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.lib;

import buildcraft.lib.client.model.ModelHolderRegistry;
import buildcraft.lib.client.render.fluid.FluidRenderer;
import buildcraft.lib.client.render.laser.LaserRenderer_BC8;
import buildcraft.lib.client.sprite.SpriteHolderRegistry;
import buildcraft.lib.misc.data.ModelVariableData;
import buildcraft.silicon.BCSilicon;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 这里面的listener有Forge Bus的和Mod Bus的 暂时先都留在这了
@Mod.EventBusSubscriber(modid = BCSilicon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum BCLibEventDist
{
    INSTANCE;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void modelBake(ModelBakeEvent event)
    {
        SpriteHolderRegistry.exportTextureMap();
        LaserRenderer_BC8.clearModels();
        ModelHolderRegistry.onModelBake();
        ModelVariableData.onModelBake();
    }

    // Mod Bus
//    @SubscribeEvent
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void textureStitchPre(TextureStitchEvent.Pre event)
    {
//        ReloadManager.INSTANCE.preReloadResources();
//        TextureMap map = event.getMap();
        TextureAtlas map = event.getAtlas();
//        SpriteHolderRegistry.onTextureStitchPre(map);
        SpriteHolderRegistry.onTextureStitchPre(event);
        ModelHolderRegistry.onTextureStitchPre(map, event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void textureStitchPreLow(TextureStitchEvent.Pre event)
    {
//        FluidRenderer.onTextureStitchPre(event.getAtlas());
        FluidRenderer.onTextureStitchPre(event);

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void textureStitchPost(TextureStitchEvent.Post event)
    {
        // Calen shouldn't map.location().equals(TextureAtlas.LOCATION_BLOCKS)
        // or the engine texture will not be loaded
        SpriteHolderRegistry.onTextureStitchPost(event);
        FluidRenderer.onTextureStitchPost(event);

//        // Calen: from #textureStitchPre
//        ModelHolderRegistry.onModelBake();
    }
}