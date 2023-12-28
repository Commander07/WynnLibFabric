package io.github.nbcss.wynnlib.data

import io.github.nbcss.wynnlib.events.EventHandler
import io.github.nbcss.wynnlib.events.WorldStateChangeEvent
import io.github.nbcss.wynnlib.utils.WorldState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext

object CharacterProfile {
    private val client = MinecraftClient.getInstance()
    var id: String? = null

    fun getLevel(): Int {
        return client.player?.experienceLevel ?: 0
    }

    fun updateId() {
        id = client.player?.inventory?.getStack(8)?.getTooltip(client.player, TooltipContext.BASIC)?.last()?.string
    }

    object WorldStateListener : EventHandler<WorldStateChangeEvent> {
        override fun handle(event: WorldStateChangeEvent) {
            println(event.state)
            if (event.state == WorldState.WORLD)
                updateId()
            else
                id = null
        }

    }
}