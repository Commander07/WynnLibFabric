package io.github.nbcss.wynnlib.utils

import io.github.nbcss.wynnlib.events.EventHandler
import io.github.nbcss.wynnlib.events.PlayerListHeaderUpdateEvent
import io.github.nbcss.wynnlib.events.PlayerReceiveChatEvent
import io.github.nbcss.wynnlib.events.WorldStateChangeEvent

enum class WorldState() {
    NOT_CONNECTED,
    LOBBY,
    WORLD,
    CHARACTER_SELECTOR,
    RESOURCE_PACK_LOADING;

    companion object {
        private var state: WorldState = NOT_CONNECTED

        fun setState(state: WorldState) {
            this.state = state
            WorldStateChangeEvent.handleEvent(WorldStateChangeEvent(this.state))
        }

        fun getState(): WorldState = state
    }

    object ChatListener : EventHandler<PlayerReceiveChatEvent> {
        override fun handle(event: PlayerReceiveChatEvent) {
            val message = event.message
            if (MessageType.getType(message) != MessageType.System)
                return
            if (message.toString().contains("Welcome to Wynncraft!"))
                setState(WORLD)
            else if (message.toString().contains("Select a character!"))
                setState(CHARACTER_SELECTOR)
            else if (message.toString().contains("Loading Resource Pack..."))
                setState(RESOURCE_PACK_LOADING)
        }
    }

    object PlayerListHeaderListener : EventHandler<PlayerListHeaderUpdateEvent> {
        override fun handle(event: PlayerListHeaderUpdateEvent) {
            if (event.footer.toString().contains("play.wynncraft.com"))
                setState(LOBBY)
        }
    }
}