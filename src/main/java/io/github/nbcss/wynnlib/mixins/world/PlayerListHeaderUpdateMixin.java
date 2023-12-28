package io.github.nbcss.wynnlib.mixins.world;

import io.github.nbcss.wynnlib.events.PlayerListHeaderUpdateEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PlayerListHeaderUpdateMixin {
    @Inject(method = "onPlayerListHeader", at = @At("HEAD"))
    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
        PlayerListHeaderUpdateEvent.Companion.handleEvent(new PlayerListHeaderUpdateEvent(packet.getHeader(), packet.getFooter()));
    }
}
