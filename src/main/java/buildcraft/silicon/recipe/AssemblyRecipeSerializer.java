package buildcraft.silicon.recipe;

import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.api.recipes.AssemblyRecipeBasic;
import buildcraft.api.recipes.AssemblyRecipeType;
import buildcraft.api.recipes.IngredientStack;
import buildcraft.datagen.factory.DistillationRecipeBuilder;
import buildcraft.datagen.silicon.AssemblyRecipeBuilder;
import buildcraft.factory.recipe.DistillationRecipeSerializer;
import buildcraft.lib.misc.JsonUtil;
import buildcraft.lib.recipe.AssemblyRecipeRegistry;
import buildcraft.lib.recipe.RefineryRecipeRegistry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class AssemblyRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AssemblyRecipe>
{
    public static final AssemblyRecipeSerializer INSTANCE;

    static
    {
        INSTANCE = new AssemblyRecipeSerializer();
        INSTANCE.setRegistryName(AssemblyRecipe.TYPE_ID);
    }

    @Override
    public AssemblyRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {
        String type = GsonHelper.getAsString(json, "type");
        AssemblyRecipeType subType = AssemblyRecipeType.valueOf(GsonHelper.getAsString(json, "subType"));
        AssemblyRecipe recipe = null;
        switch (subType)
        {
            case BASIC:
                long requiredMicroJoules = json.get("requiredMicroJoules").getAsLong();
                JsonArray requiredStacks = json.get("requiredStacks").getAsJsonArray();
                List<IngredientStack> requiredStacksList = Lists.newArrayList();
                requiredStacks.forEach(j -> requiredStacksList.add(JsonUtil.deSerializeIngredientStack(j.getAsJsonObject())));
                ItemStack output = JsonUtil.deSerializeItemStack(json.getAsJsonObject("output"));
                recipe = new AssemblyRecipeBasic(recipeId, requiredMicroJoules, ImmutableSet.copyOf(requiredStacksList), output);
                break;
            case FACADE:
                recipe = FacadeAssemblyRecipes.INSTANCE;
                break;
        }
        return recipe;
    }

    public static void toJson(AssemblyRecipeBuilder builder, JsonObject json)
    {
        json.addProperty("type", AssemblyRecipe.TYPE_ID.toString());
        json.addProperty("subType", builder.type.name());
        switch (builder.type)
        {
            case BASIC:
                json.addProperty("requiredMicroJoules", builder.requiredMicroJoules);
                JsonArray requiredStacks = new JsonArray();
                builder.requiredStacks.forEach(ingredientStack -> requiredStacks.add(JsonUtil.serializeIngredientStack(ingredientStack)));
                json.add("requiredStacks", requiredStacks);
                json.add("output", JsonUtil.serializeItemStack(builder.output));
                break;
            case FACADE:
                break;
        }
    }

    @Nullable
    @Override
    public AssemblyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        AssemblyRecipe recipe = null;
        switch (buffer.readEnum(AssemblyRecipeType.class))
        {
            case BASIC:
                long requiredMicroJoules = buffer.readLong();
                Set<IngredientStack> requiredStacks = Sets.newHashSet();
                int ingredientSize = buffer.readInt();
                for (int index = 0; index < ingredientSize; index++)
                {
                    requiredStacks.add(IngredientStack.fromNetwork(buffer));
                }
                ItemStack output = buffer.readItem();
                recipe = new AssemblyRecipeBasic(recipeId, requiredMicroJoules, ImmutableSet.copyOf(requiredStacks), output);
                break;
            case FACADE:
                recipe = FacadeAssemblyRecipes.INSTANCE;
        }
        return recipe;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, AssemblyRecipe recipe)
    {
        if (recipe instanceof AssemblyRecipeBasic)
        {
            buffer.writeEnum(AssemblyRecipeType.BASIC);
            buffer.writeLong(recipe.getRequiredMicroJoulesForSerialize());
            Set<IngredientStack> requiredStacks = recipe.getRequiredIngredientStacksForSerialize();
            buffer.writeInt(requiredStacks.size());
            requiredStacks.forEach(ingredientStack -> ingredientStack.toNetwork(buffer));
            ItemStack output = recipe.getOutputForSerialize().toArray(new ItemStack[0])[0];
            buffer.writeItemStack(output, false);
        }
        else if (recipe instanceof FacadeAssemblyRecipes)
        {
            buffer.writeEnum(AssemblyRecipeType.FACADE);
        }
    }
}