package buildcraft.datagen.transport;

import buildcraft.datagen.base.BCBaseBlockStateGenerator;
import buildcraft.transport.BCTransport;
import buildcraft.transport.BCTransportBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class TransportBlockStateGenerator extends BCBaseBlockStateGenerator {
    public TransportBlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BCTransport.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // filteredBuffer
        getVariantBuilder(BCTransportBlocks.filteredBuffer.get()).forAllStates(s ->
                ConfiguredModel.builder().modelFile(
                                models().withExistingParent(BCTransportBlocks.filteredBuffer.get().getRegistryName().toString(), CUBE_ALL)
                                        .texture("all", "buildcrafttransport:block/filtered_buffer/default")
                                        .texture("particle", "buildcrafttransport:block/filtered_buffer/default")
                        )
                        .build()
        );

        // pipeHolder
        builtinEntity(BCTransportBlocks.pipeHolder.get());
    }

    @NotNull
    @Override
    public String getName() {
        return "BuildCraft Transport BlockState Generator";
    }
}
