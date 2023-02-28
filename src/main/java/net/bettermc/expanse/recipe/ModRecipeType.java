package net.bettermc.expanse.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ModRecipeType<T extends Recipe<Inventory>> implements RecipeType<T> {
    private final Identifier id;
    private List<T> cached;

    public ModRecipeType(Identifier id) {
        this.id = id;
        this.cached = null;
    }

    public Identifier getId() {
        return this.id;
    }

    public T findFirst(World world, Predicate<T> filter) {
        return this.filter(world, filter).findFirst().orElse(null);
    }

    public Stream<T> filter(World world, Predicate<T> filter) {
        return this.getRecipes(world).stream().filter(filter);
    }

    public List<T> getRecipes(World world) {
        this.cached = null;
        RecipeManager recipeManager = world.getRecipeManager();
        this.cached = recipeManager.listAllOfType(this);

        return this.cached;
    }
}