package io.github.nbcss.wynnlib.mixins.analysis;

import io.github.nbcss.wynnlib.Settings;
import io.github.nbcss.wynnlib.function.AnalyzeMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
public abstract class InventoryAnalysisMixin {

    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow @Final protected ScreenHandler handler;

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    public void drawTooltip(DrawContext context, int x, int y, CallbackInfo ci){
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack stack = this.focusedSlot.getStack();
            if(stack != null && Settings.INSTANCE.getOption(Settings.SettingOption.ANALYZE_MODE)){
                List<Text> tooltip = AnalyzeMode.INSTANCE.getAnalyzeResult(stack);
                if (tooltip != null) {
                    context.drawTooltip(textRenderer, tooltip, stack.getTooltipData(), x, y);
                    ci.cancel();
                }
            }
        }
    }
}