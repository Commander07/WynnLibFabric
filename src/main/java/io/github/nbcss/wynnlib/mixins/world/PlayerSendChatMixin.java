package io.github.nbcss.wynnlib.mixins.world;

import io.github.nbcss.wynnlib.events.PlayerSendChatEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class PlayerSendChatMixin {
    @Inject(method = "sendMessage(Lnet/minecraft/text/Text;)V", at = @At(value = "HEAD"), cancellable = true)
    public void sendChatMessage(Text message, CallbackInfo ci) {
        PlayerSendChatEvent event = new PlayerSendChatEvent((ClientPlayerEntity) (Object) this, message.toString());
        PlayerSendChatEvent.Companion.handleEvent(event);
        if (event.getCancelled()){
            ci.cancel();
        }
    }
}
