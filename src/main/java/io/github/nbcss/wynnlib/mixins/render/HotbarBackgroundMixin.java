package io.github.nbcss.wynnlib.mixins.render;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.nbcss.wynnlib.Settings;
import io.github.nbcss.wynnlib.events.RenderItemOverrideEvent;
import io.github.nbcss.wynnlib.matcher.MatchableItem;
import io.github.nbcss.wynnlib.matcher.item.ItemMatcher;
import io.github.nbcss.wynnlib.render.RenderKit;
import io.github.nbcss.wynnlib.utils.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
public class HotbarBackgroundMixin {
    private final Identifier texture = new Identifier("wynnlib", "textures/legacy/lock.png");
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Shadow @Final private static Identifier HOTBAR_TEXTURE;
    private boolean flag = false;
    DrawContext context = null;

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    public void renderHotbarHead(float tickDelta, DrawContext context, CallbackInfo ci){
        flag = true;
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
            shift = At.Shift.AFTER))
    public void renderHotbar(float tickDelta, DrawContext context, CallbackInfo ci){
        this.context = context;
        if(flag){
            flag = false;
            drawSlots(context);
        }
    }

    @Redirect(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"))
    public void renderHotbarItem(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (context == null)
            this.context = instance;
        if (drawOverrides(textRenderer, stack, x, y))
            return;
        instance.drawItemInSlot(textRenderer, stack, x, y);
    }

    private boolean drawOverrides(TextRenderer renderer, ItemStack stack, int x, int y) {
        RenderItemOverrideEvent event = new RenderItemOverrideEvent(context, renderer, stack, x, y);
        RenderItemOverrideEvent.Companion.handleEvent(event);
        return event.getCancelled();
    }

    private void drawSlots(DrawContext context){
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null){
            int distance = 0;
            if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer x) {
                distance = x;
            }
            int y = this.scaledHeight - 19 - distance;
            for(int i = 0; i < 6; i++) {
                int x = this.scaledWidth / 2 - 90 + i * 20 + 2;
                ItemStack stack = playerEntity.getInventory().main.get(i);
                if (Settings.INSTANCE.getOption(Settings.SettingOption.ITEM_BACKGROUND_COLOR)){
                    MatchableItem item = ItemMatcher.Companion.toItem(stack);
                    if(item != null){
                        RenderSystem.disableDepthTest();
                        Color color = item.getMatcherType().getColor();
                        context.fill(x, y, x + 16, y + 16, color.withAlpha(0xCC).code());
                    }
                }
                if (playerEntity.experienceLevel > 0 && Settings.INSTANCE.isSlotLocked(36 + i)) {
                    RenderSystem.enableBlend();
                    RenderSystem.disableDepthTest();
                    RenderKit.INSTANCE.renderTexture(context, texture, x - 2, y - 2,
                            0, 0, 20, 20, 20, 20);
                }
            }
        }
    }
}
