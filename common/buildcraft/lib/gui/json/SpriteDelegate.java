package buildcraft.lib.gui.json;

import buildcraft.api.core.render.ISprite;
import buildcraft.lib.client.sprite.SpriteAtlas;
import buildcraft.lib.misc.SpriteUtil;

public class SpriteDelegate implements ISprite {
    public ISprite delegate;

    public SpriteDelegate(ISprite delegate) {
        this.delegate = delegate;
    }

    public SpriteDelegate() {
//        this(new SpriteAtlas(Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite()));
//        this(new SpriteAtlas(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_PARTICLES).apply(new ResourceLocation("missingno"))));
        this(new SpriteAtlas(SpriteUtil.missingSprite()));
    }

    @Override
    public void bindTexture() {
        if (delegate != null) {
            delegate.bindTexture();
        }
    }

    @Override
    public double getInterpU(double u) {
        return delegate == null ? 0 : delegate.getInterpU(u);
    }

    @Override
    public double getInterpV(double v) {
        return delegate == null ? 0 : delegate.getInterpV(v);
    }
}
