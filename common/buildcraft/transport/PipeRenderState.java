/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.transport;

import io.netty.buffer.ByteBuf;

import buildcraft.api.core.ISerializable;
import buildcraft.transport.utils.ConnectionMatrix;
import buildcraft.transport.utils.TextureMatrix;
import buildcraft.transport.utils.WireMatrix;

public class PipeRenderState implements ISerializable {

	public final ConnectionMatrix pipeConnectionMatrix = new ConnectionMatrix();
	public final TextureMatrix textureMatrix = new TextureMatrix();
	public final WireMatrix wireMatrix = new WireMatrix();
	public byte glassColor = -1;
	
	private boolean dirty = true;

	public void clean() {
		dirty = false;
		pipeConnectionMatrix.clean();
		textureMatrix.clean();
		wireMatrix.clean();
	}

	public boolean isDirty() {
		return dirty || pipeConnectionMatrix.isDirty()
				|| textureMatrix.isDirty() || wireMatrix.isDirty();
	}

	public boolean needsRenderUpdate() {
		return pipeConnectionMatrix.isDirty() || textureMatrix.isDirty()
				|| wireMatrix.isDirty();
	}

	@Override
	public void writeData(ByteBuf data) {
		data.writeByte(glassColor);
		pipeConnectionMatrix.writeData(data);
		textureMatrix.writeData(data);
		wireMatrix.writeData(data);
	}

	@Override
	public void readData(ByteBuf data) {
		glassColor = data.readByte();
		pipeConnectionMatrix.readData(data);
		textureMatrix.readData(data);
		wireMatrix.readData(data);
	}
}
