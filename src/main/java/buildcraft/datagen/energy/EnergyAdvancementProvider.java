package buildcraft.datagen.energy;

import buildcraft.core.BCCoreBlocks;
import buildcraft.datagen.base.BCBaseAdvancementProvider;
import buildcraft.energy.BCEnergy;
import buildcraft.energy.BCEnergyBlocks;
import buildcraft.datagen.core.CoreAdvancementProvider;
import buildcraft.energy.generation.biome.BCBiomeRegistry;
import buildcraft.energy.BCEnergyFluids;
import buildcraft.core.BCCoreItems;
import buildcraft.energy.BCEnergyItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

// Calen Completed
public class EnergyAdvancementProvider extends BCBaseAdvancementProvider
{
    private static final String NAMESPACE = BCEnergy.MOD_ID;
//    private static Advancement ROOT = CoreAdvancementProvider.ROOT;
    private static Advancement ROOT;
//    private static Advancement GUIDE = CoreAdvancementProvider.GUIDE;
    private static Advancement GUIDE;

    private static final ImpossibleTrigger.TriggerInstance IMPOSSIBLE = new ImpossibleTrigger.TriggerInstance();

    public static void registerEnergyAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper)
    {
        ROOT = CoreAdvancementProvider.ROOT;
        GUIDE = CoreAdvancementProvider.GUIDE;
        // engine
        Advancement engine = Advancement.Builder.advancement().display(
                        BCEnergyBlocks.engineStone.get(),
                        new TranslatableComponent("advancements.buildcraftcore.engine.title"),
                        new TranslatableComponent("advancements.buildcraftcore.engine.description"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .parent(GUIDE)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "redstone_engine",
                        InventoryChangeTrigger.TriggerInstance.hasItems(BCCoreBlocks.engineWood.get())
                )
                .addCriterion(
                        "strirling_engine",
                        InventoryChangeTrigger.TriggerInstance.hasItems(BCEnergyBlocks.engineStone.get())
                )
                .addCriterion(
                        "combustion_engine",
                        InventoryChangeTrigger.TriggerInstance.hasItems(BCEnergyBlocks.engineIron.get())
                )
                .save(consumer, NAMESPACE + ":engine");
        // powering_up
        Advancement powering_up = Advancement.Builder.advancement().display(
                        BCEnergyBlocks.engineStone.get(),
                        new TranslatableComponent("advancements.buildcraftenergy.poweringUp.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.poweringUp.description"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .parent(engine)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "code_trigger",
                        IMPOSSIBLE
                )
                .save(consumer, NAMESPACE + ":powering_up");
        // lava_power
        Advancement lava_power = Advancement.Builder.advancement().display(
                        Items.LAVA_BUCKET,
                        new TranslatableComponent("advancements.buildcraftenergy.lava_power.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.lava_power.description"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .parent(engine)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "code_trigger",
                        IMPOSSIBLE
                )
                .save(consumer, NAMESPACE + ":lava_power");
        // ice_cool
        Advancement ice_cool = Advancement.Builder.advancement().display(
                        Items.WATER_BUCKET,
                        new TranslatableComponent("advancements.buildcraftenergy.ice_cool.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.ice_cool.description"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .parent(powering_up)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "code_trigger",
                        IMPOSSIBLE
                )
                .save(consumer, NAMESPACE + ":ice_cool");
        // fine_riches
        Advancement fine_riches = Advancement.Builder.advancement().display(
                        BCEnergyItems.globOil.get(),
                        new TranslatableComponent("advancements.buildcraftenergy.fine_riches.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.fine_riches.description"),
                        null,
                        FrameType.GOAL,
                        true, true, false
                )
                .parent(ROOT)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "oil_desert_biome",
                        LocationTrigger.TriggerInstance.located(
                                LocationPredicate.inBiome(BCBiomeRegistry.RESOURCE_KEY_BIOME_OIL_DESERT)
                        )
                )
                .addCriterion(
                        "oil_ocean_biome",
                        LocationTrigger.TriggerInstance.located(
                                LocationPredicate.inBiome(BCBiomeRegistry.RESOURCE_KEY_BIOME_OIL_OCEAN)
                        )
                )
                .save(consumer, NAMESPACE + ":fine_riches");
        // sticky_dipping
        Advancement sticky_dipping = Advancement.Builder.advancement().display(
                        BCEnergyItems.globOil.get(),
                        new TranslatableComponent("advancements.buildcraftenergy.sticky_dipping.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.sticky_dipping.description"),
                        null,
                        FrameType.TASK,
                        true, true, true
                )
                .parent(fine_riches)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "oil",
                        EnterBlockTrigger.TriggerInstance.entersBlock(BCEnergyFluids.crudeOil[0].get().getReg().getBlock())
                )
                .save(consumer, NAMESPACE + ":sticky_dipping");
        // refine_and_redefine
        Advancement refine_and_redefine = Advancement.Builder.advancement().display(
                        BCEnergyItems.globOil.get(),
                        new TranslatableComponent("advancements.buildcraftenergy.refine_and_redefine.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.refine_and_redefine.description"),
                        null,
                        FrameType.CHALLENGE,
                        true, true, false
                )
                .parent(sticky_dipping)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "code_trigger",
                        IMPOSSIBLE
                )
                .save(consumer, NAMESPACE + ":refine_and_redefine");
        // to_much_power
        Advancement to_much_power = Advancement.Builder.advancement().display(
                        BCCoreItems.wrench.get(),
                        new TranslatableComponent("advancements.buildcraftenergy.to_much_power.title"),
                        new TranslatableComponent("advancements.buildcraftenergy.to_much_power.description"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .parent(powering_up)
                .requirements(RequirementsStrategy.OR)
                .addCriterion(
                        "code_trigger",
                        IMPOSSIBLE
                )
                .save(consumer, NAMESPACE + ":to_much_power");
    }
}