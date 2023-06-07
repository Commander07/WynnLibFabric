package io.github.nbcss.wynnlib.mixins.render;

import io.github.nbcss.wynnlib.Settings;
import io.github.nbcss.wynnlib.events.DrawSlotEvent;
import io.github.nbcss.wynnlib.events.RenderItemOverrideEvent;
import io.github.nbcss.wynnlib.matcher.MatchableItem;
import io.github.nbcss.wynnlib.matcher.item.ItemMatcher;
import io.github.nbcss.wynnlib.render.RenderKit;
import io.github.nbcss.wynnlib.utils.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class ItemBackgroundMixin extends Screen {
    final Identifier TEXTURE = new Identifier("wynnlib", "textures/slot/circle.png");
    DrawContext drawContext = null;

    protected ItemBackgroundMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        drawContext = context;
    }

    @Inject(method = "drawItem", at = @At("HEAD"))
    public void drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        drawColorSlot(stack, x, y);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        DrawSlotEvent event = new DrawSlotEvent((HandledScreen<?>) (Object) this, context, slot);
        DrawSlotEvent.Companion.handleEvent(event);
    }

    @Redirect(method = "drawItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    public void drawItemInvoke(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride) {
        if (drawOverrides(textRenderer, stack, x, y))
            return;
        instance.drawItemInSlot(textRenderer, stack, x, y, countOverride);
    }

    @Redirect(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    public void redirect(DrawContext instance, ItemStack stack, int x, int y, int seed){
        drawColorSlot(stack, x, y);
        instance.drawItem(stack, x, y, seed);
    }

    @Redirect(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    public void redirect(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride){
        if (drawOverrides(textRenderer, stack, x, y))
            return;
        instance.drawItemInSlot(textRenderer, stack, x, y, countOverride);
    }

    private boolean drawOverrides(TextRenderer renderer, ItemStack stack, int x, int y) {
        RenderItemOverrideEvent event = new RenderItemOverrideEvent(drawContext, renderer, stack, x, y);
        RenderItemOverrideEvent.Companion.handleEvent(event);
        return event.getCancelled();
    }

    private void drawColorSlot(ItemStack stack, int x, int y) {
        if (!Settings.INSTANCE.getOption(Settings.SettingOption.ITEM_BACKGROUND_COLOR))
            return;
        MatchableItem item = ItemMatcher.Companion.toItem(stack);
        if(item != null) {
            Color color = item.getMatcherType().getColor();
            RenderKit.INSTANCE.renderTextureWithColor(drawContext, TEXTURE, color.solid(),
                    x - 2, y - 2, 0, 0, 20, 20, 20, 20);
        }
    }
}
