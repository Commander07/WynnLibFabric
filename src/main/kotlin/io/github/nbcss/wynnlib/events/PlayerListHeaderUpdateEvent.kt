package io.github.nbcss.wynnlib.events

import net.minecraft.text.Text

class PlayerListHeaderUpdateEvent(val header: Text, val footer: Text) {
    companion object: EventHandler.HandlerList<PlayerListHeaderUpdateEvent>()
}