/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.builders.client.render;

import buildcraft.builders.tile.TileQuarry;
import buildcraft.lib.chunkload.ChunkLoaderManager;
import buildcraft.lib.client.render.DetachedRenderer;
import buildcraft.lib.debug.DebugRenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdvDebuggerQuarry implements DetachedRenderer.IDetachedRenderer
{
    private static final int COLOUR_CHUNK = 0x55_99_FF_99;

    private final WeakReference<TileQuarry> tileReference;

    public AdvDebuggerQuarry(TileQuarry tile)
    {
        tileReference = new WeakReference<>(tile);
    }

    @Override
//    public void render(Player player, float partialTicks)
    public void render(Player player, float partialTicks, PoseStack poseStack)
    {
        TileQuarry tile = tileReference.get();
        if (tile == null || !tile.frameBox.isInitialized())
        {
            return;
        }
        List<ChunkPos> chunkPoses = new ArrayList<>(ChunkLoaderManager.getChunksToLoad(tile));
        chunkPoses.sort(
                Comparator.comparingDouble(chunkPos ->
//                        -player.getPositionEyes(partialTicks).distanceTo(
                        -player.getEyePosition(partialTicks).distanceTo(
                                new Vec3(
//                                        chunkPos.getXStart() + 0.5 + (chunkPos.getXEnd() - chunkPos.getXStart()) / 2,
                                        chunkPos.getMinBlockX() + 0.5 + (chunkPos.getMaxBlockX() - chunkPos.getMinBlockX()) / 2,
//                                        player.getPositionEyes(partialTicks).y,
                                        player.getEyePosition(partialTicks).y,
//                                        chunkPos.getZStart() + 0.5 + (chunkPos.getZEnd() - chunkPos.getZStart()) / 2
                                        chunkPos.getMinBlockZ() + 0.5 + (chunkPos.getMaxBlockZ() - chunkPos.getMinBlockZ()) / 2
                                )
                        )
                )
        );
//        GlStateManager.enableBlend();
        RenderSystem.enableBlend();
//        BufferBuilder bb = Tessellator.getInstance().getBuffer();
//        BufferBuilder bb = Tesselator.getInstance().getBuilder();
//        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
//        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(Sheets.translucentCullBlockSheet());
        for (ChunkPos chunkPos : chunkPoses)
        {
            DebugRenderHelper.renderAABB(
                    poseStack,
                    bb,
                    new AABB(
//                            chunkPos.getXStart() + 0.5D,
                            chunkPos.getMinBlockX() + 0.5D,
                            tile.frameBox.min().getY() + 0.5D,
//                            chunkPos.getZStart() + 0.5D,
                            chunkPos.getMinBlockZ() + 0.5D,
//                            chunkPos.getXEnd() + 0.5D,
                            chunkPos.getMaxBlockX() + 0.5D,
                            tile.frameBox.max().getY() + 0.5D,
//                            chunkPos.getZEnd() + 0.5D
                            chunkPos.getMaxBlockZ() + 0.5D
                    ),
                    COLOUR_CHUNK
            );
        }
//        Tessellator.getInstance().draw();
//        Tesselator.getInstance().end();
//        GlStateManager.disableBlend();
        RenderSystem.disableBlend();
    }
}
