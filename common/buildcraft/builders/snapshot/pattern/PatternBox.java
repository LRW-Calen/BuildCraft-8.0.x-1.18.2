/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.builders.snapshot.pattern;

import buildcraft.api.filler.IFilledTemplate;
import buildcraft.api.filler.IFillerPatternShape;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.builders.BCBuildersSprites;
import buildcraft.lib.client.sprite.SpriteHolderRegistry.SpriteHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PatternBox extends Pattern implements IFillerPatternShape {
    public PatternBox() {
        super("box");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteHolder getSprite() {
        return BCBuildersSprites.FILLER_BOX;
    }

    @Override
    public boolean fillTemplate(IFilledTemplate filledTemplate, IStatementParameter[] params) {
        filledTemplate.setPlaneYZ(0, true);
        filledTemplate.setPlaneYZ(filledTemplate.getMax().getX(), true);
        filledTemplate.setPlaneXZ(0, true);
        filledTemplate.setPlaneXZ(filledTemplate.getMax().getY(), true);
        filledTemplate.setPlaneXY(0, true);
        filledTemplate.setPlaneXY(filledTemplate.getMax().getZ(), true);
        return true;
    }
}
