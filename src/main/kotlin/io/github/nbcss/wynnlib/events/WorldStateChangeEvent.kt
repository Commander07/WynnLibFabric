package io.github.nbcss.wynnlib.events

import io.github.nbcss.wynnlib.utils.WorldState

class WorldStateChangeEvent(val state: WorldState) {
    companion object: EventHandler.HandlerList<WorldStateChangeEvent>()
}