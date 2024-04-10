/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.client.render.tile;

import buildcraft.lib.client.model.MutableVertex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector4d;

/**
 * A variable sized element (like LED) that can render somewhere in a TESR. Passing a resolver instance will let you
 * modify the location, colour, lightmap, and size of the single element. This does not allow for different textures.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPartCube
{
    /**
     * The centre of this element.
     */
    public final MutableVertex center = new MutableVertex();
    public double sizeX = 1 / 16.0, sizeY = 1 / 16.0, sizeZ = 1 / 16.0;

    /**
     * Constructs a simple cube configured for a LED.
     */
    public RenderPartCube()
    {
        this(1 / 16.0, 1 / 16.0, 1 / 16.0);
    }

    public RenderPartCube(double x, double y, double z)
    {
        center.positiond(x, y, z);
    }

    public void setWhiteTex()
    {
        // Calen: at TextureStitchEvent.Post ForgeModelBakery.White.instance() cannot be called
        // RuntimeException: getAtlasTexture called too early! (ModelManager.java:99)
////        TextureAtlasSprite sprite = ModelLoader.White.INSTANCE;
        TextureAtlasSprite sprite = ForgeModelBakery.White.instance();
//        // Reset the vertex so that edits don't spill out to other tiles.
//        center.texf(sprite.getInterpolatedU(8), sprite.getInterpolatedV(8));
        center.texf(sprite.getU(8), sprite.getV(8));
    }

    /**
     * Renders an element, without changing the vertex. However this does ignore the "normal" and "texture" components
     * of the vertex.
     */
//    public void render(BufferBuilder bb)
    public void render(PoseStack poseStack, VertexConsumer bb)
    {
        Point3f pos = center.positionvf();
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        // Calen: add 0.001 to avoid black border caused by VertexConsumer#vertex: double->float->double
//        double rX = sizeX / 2;
        double rX = sizeX / 2 + 0.001;
//        double rY = sizeY / 2;
        double rY = sizeY / 2 + 0.001;
//        double rZ = sizeZ / 2;
        double rZ = sizeZ / 2 + 0.001;

        vertex(poseStack, bb, center, x - rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z - rZ);
        vertex(poseStack, bb, center, x - rX, y + rY, z - rZ);

        vertex(poseStack, bb, center, x - rX, y - rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y - rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y - rY, z + rZ);
        vertex(poseStack, bb, center, x - rX, y - rY, z + rZ);

        vertex(poseStack, bb, center, x - rX, y - rY, z + rZ);
        vertex(poseStack, bb, center, x - rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x - rX, y + rY, z - rZ);
        vertex(poseStack, bb, center, x - rX, y - rY, z - rZ);

        vertex(poseStack, bb, center, x + rX, y - rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x + rX, y - rY, z + rZ);

        vertex(poseStack, bb, center, x - rX, y - rY, z - rZ);
        vertex(poseStack, bb, center, x - rX, y + rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z - rZ);
        vertex(poseStack, bb, center, x + rX, y - rY, z - rZ);

        vertex(poseStack, bb, center, x + rX, y - rY, z + rZ);
        vertex(poseStack, bb, center, x + rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x - rX, y + rY, z + rZ);
        vertex(poseStack, bb, center, x - rX, y - rY, z + rZ);
    }

    //    private static void vertex(BufferBuilder vb, MutableVertex center, double x, double y, double z)
    private static void vertex(PoseStack poseStack, VertexConsumer vb, MutableVertex center, double x, double y, double z)
    {
        // Using DefaultVertexFormats.BLOCK
        // -- POSITION_3F // pos
        // -- COLOR_4UB // colour
        // -- TEX_2F // texture
        // -- TEX_2S // lightmap
//        vb.pos(x, y, z);
        vb.vertex(poseStack.last().pose(), (float) x, (float) y, (float) z);
        center.renderColour(vb);
        center.renderTex(vb);
        center.renderOverlay(vb); // Calen add
        center.renderLightMap(vb);
        center.renderNormal(poseStack.last().normal(), vb); // Calen add
        vb.endVertex();
    }

    // Calen test
//    private static Vector4d transformDouble(double x, double y, double z, double w, Matrix4d p_123608_)
//    {
//        double f = x;
//        double f1 = y;
//        double f2 = z;
//        double f3 = w;
//        x = p_123608_.m00 * f + p_123608_.m01 * f1 + p_123608_.m02 * f2 + p_123608_.m03 * f3;
//        y = p_123608_.m10 * f + p_123608_.m11 * f1 + p_123608_.m12 * f2 + p_123608_.m13 * f3;
//        z = p_123608_.m20 * f + p_123608_.m21 * f1 + p_123608_.m22 * f2 + p_123608_.m23 * f3;
//        w = p_123608_.m30 * f + p_123608_.m31 * f1 + p_123608_.m32 * f2 + p_123608_.m33 * f3;
//        return new Vector4d(x, y, z, w);
//    }
}
