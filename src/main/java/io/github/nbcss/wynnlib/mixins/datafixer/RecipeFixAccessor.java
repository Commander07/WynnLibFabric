package io.github.nbcss.wynnlib.mixins.datafixer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.datafixer.mapping.FlatteningRecipeMapping;

@Mixin(FlatteningRecipeMapping.class)
public interface RecipeFixAccessor {

    @Accessor("RECIPES")
    static Map<String, String> getRECIPES() {
        throw new AssertionError();
    }
}