/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.silicon.client.render;

import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.silicon.tile.TileLaser;
import buildcraft.silicon.BCSiliconBlocks;
import buildcraft.lib.client.render.DetachedRenderer;
import buildcraft.lib.debug.DebugRenderHelper;
import buildcraft.lib.misc.VolumeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AdvDebuggerLaser implements DetachedRenderer.IDetachedRenderer
{
    private static final int COLOUR_VISIBLE = 0xFF_99_FF_99;
    private static final int COLOUR_NOT_VISIBLE = 0xFF_11_11_99;

    private final BlockPos pos;
    private final Direction face;

    public AdvDebuggerLaser(TileLaser tile)
    {
        pos = tile.getBlockPos();
        BlockState state = tile.getLevel().getBlockState(pos);
        face = state.getBlock() == BCSiliconBlocks.laser.get()
                ? state.getValue(BuildCraftProperties.BLOCK_FACING_6)
                : null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Player player, float partialTicks, PoseStack poseStack)
    {
        if (pos == null || face == null)
        {
            return;
        }
//        BufferBuilder bb = Tessellator.getInstance().getBuffer();
//        BufferBuilder bb = Tesselator.getInstance().getBuilder();
//        RenderSystem.setShader(GameRenderer::getBlockShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableBlend();
//        RenderSystem.setShaderColor(1,1,1,1);
//        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
//        VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(Sheets.translucentCullBlockSheet());
        VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(Sheets.translucentCullBlockSheet());
//        VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lightning());
//        VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.leash());
        VolumeUtil.iterateCone(player.level, pos, face, 6, true, (world, start, p, visible) ->
        {
            int colour = visible ? COLOUR_VISIBLE : COLOUR_NOT_VISIBLE;
//            DebugRenderHelper.renderSmallCuboid(poseStack, bb, p, colour);
            DebugRenderHelper.renderSmallCuboid(poseStack, bb, p, colour);
        });
//        Tessellator.getInstance().draw();
//        Tesselator.getInstance().end();
    }
}
