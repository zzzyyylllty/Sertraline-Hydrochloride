package io.github.zzzyyylllty.sertraline.recipe

//import com.google.common.collect.Maps
//import net.minecraft.core.NonNullList
//import net.minecraft.world.item.crafting.*
//import net.minecraft.world.level.Level
//import net.momirealms.craftengine.core.item.ItemBuildContext
//import net.momirealms.craftengine.core.item.recipe.CustomShapedRecipe
//import org.bukkit.inventory.ItemStack
//
//class InjectedShapedRecipe(
//    private val recipe: CustomShapedRecipe<ItemStack>,
//    private val roughPattern: ShapedRecipePattern,
//    group: String,
//    category: CraftingBookCategory,
//    visualPattern: ShapedRecipePattern,
//    result: net.minecraft.world.item.ItemStack,
//    showNotification: Boolean
//) : ShapedRecipe(group, category, visualPattern, result, showNotification) {
//
//    companion object {
//        @JvmStatic
//        fun of(recipe: CustomShapedRecipe<ItemStack>): InjectedShapedRecipe {
//            // Create the visual pattern (for display)
//            val visual: Map<Char, Ingredient> = Maps.transformValues(
//                recipe.pattern().ingredients()
//            ) { RecipeHelper.toMinecraftVisual(it) }
//            val visualPattern = ShapedRecipePattern.of(visual, recipe.pattern().pattern())
//
//            // Create the actual matching pattern
//            val actual: Map<Char, Ingredient> = Maps.transformValues(
//                recipe.pattern().ingredients()
//            ) { RecipeHelper.toMinecraft(it) }
//            val actualPattern = ShapedRecipePattern.of(actual, recipe.pattern().pattern())
//
//            return InjectedShapedRecipe(
//                recipe = recipe,
//                roughPattern = actualPattern,
//                group = recipe.group(),
//                category = RecipeHelper.toMinecraft(recipe.category()),
//                visualPattern = visualPattern,
//                result = recipe.buildVisualOrActualResult(ItemBuildContext.empty())
//                    .literalObject as net.minecraft.world.item.ItemStack,
//                showNotification = recipe.showNotification()
//            )
//        }
//    }
//
//    override fun matches(input: CraftingInput, level: Level): Boolean {
//        // First do a rough match
//        val vanillaMatches = roughPattern.matches(input)
//        if (!vanillaMatches) return false
//
//        // Then do a detailed match
//        return recipe.matches(RecipeHelper.toCraftEngine(input))
//    }
//
//    override fun getRemainingItems(input: CraftingInput): NonNullList<net.minecraft.world.item.ItemStack> {
//        return RecipeHelper.getRemainingItems(recipe.id(), input)
//    }
//
//    override fun showNotification(): Boolean {
//        return recipe.showNotification()
//    }
//}