package buildcraft.transport;

import buildcraft.builders.BCBuildersItems;
import buildcraft.builders.item.ItemSnapshot;
import buildcraft.core.BCCore;
import buildcraft.lib.misc.ColourUtil;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCTransport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCTransportModBusEventDist
{
    @SubscribeEvent
    public static void itemPropReg(FMLClientSetupEvent event)
    {
        event.enqueueWork(
                () ->
                {
                    ItemProperties.register(
                            BCTransportItems.wire.get(),
                            new ResourceLocation("buildcraft:colour"),
                            (stack, world, entity, pSeed) -> ColourUtil.getStackColourIdFromTag(stack)
                    );
                }
        );
    }
}