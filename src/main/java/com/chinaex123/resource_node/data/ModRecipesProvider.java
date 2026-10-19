package com.chinaex123.resource_node.data;

import com.chinaex123.resource_node.init.RNBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNBlocks.RESOURCE_NODE.get())
                .pattern("BCB")
                .pattern("CAC")
                .pattern("BCB")
                .define('A', Items.BEACON)
                .define('B', Tags.Items.INGOTS_NETHERITE)
                .define('C', Tags.Items.NETHER_STARS)
                .unlockedBy(getHasName(Items.BEACON), has(Items.BEACON))
                .unlockedBy(getHasName(Items.DIAMOND), has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(getHasName(Items.NETHER_STAR), has(Tags.Items.NETHER_STARS))
                .save(recipeOutput);

    }
}
