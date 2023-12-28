package io.github.nbcss.wynnlib.mixins.world;

import io.github.nbcss.wynnlib.events.PlayerReceiveChatEvent;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class PlayerReceiveChatMixin {
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!overlay)
            PlayerReceiveChatEvent.Companion.handleEvent(new PlayerReceiveChatEvent(message));
    }
}
