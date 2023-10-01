package io.github.nbcss.wynnlib.mixins;

import io.github.nbcss.wynnlib.events.PacketSendingEvent;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class NetworkMixin {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        PacketSendingEvent event = new PacketSendingEvent(packet);
        PacketSendingEvent.Companion.handleEvent(event);
        if (event.getCancelled()) {
            ci.cancel();
        }
    }
}
