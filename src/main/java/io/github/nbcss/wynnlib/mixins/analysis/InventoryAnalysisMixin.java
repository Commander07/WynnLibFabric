package io.github.nbcss.wynnlib.mixins.analysis;

import io.github.nbcss.wynnlib.Settings;
import io.github.nbcss.wynnlib.function.AnalyzeMode;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public abstract class InventoryAnalysisMixin {
    @Inject(method = "drawItemTooltip", at = @At("HEAD"), cancellable = true)
    public void drawTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci){
        if(stack != null && Settings.INSTANCE.getOption(Settings.SettingOption.ANALYZE_MODE)){
            List<Text> tooltip = AnalyzeMode.INSTANCE.getAnalyzeResult(stack);
            if (tooltip != null) {
                drawTooltip(textRenderer, tooltip, stack.getTooltipData(), x, y);
                ci.cancel();
            }
        }
    }

    @Shadow
    public abstract void drawTooltip(TextRenderer textRenderer, List<Text> lines, Optional<TooltipData> data, int x, int y);
}