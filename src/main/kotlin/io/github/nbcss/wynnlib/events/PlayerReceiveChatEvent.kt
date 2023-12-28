package io.github.nbcss.wynnlib.events

import net.minecraft.text.Text

class PlayerReceiveChatEvent(val message: Text): CancellableEvent() {
    companion object: EventHandler.HandlerList<PlayerReceiveChatEvent>()
}