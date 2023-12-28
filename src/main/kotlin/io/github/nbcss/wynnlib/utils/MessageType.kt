package io.github.nbcss.wynnlib.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

enum class MessageType {
    Chat,
    System;
    companion object {
        fun getType(message: Text): MessageType {
            if (WorldState.getState() == WorldState.LOBBY) {
                return if (message.siblings.size == 3 && MinecraftClient.getInstance().networkHandler?.getPlayerListEntry(message.siblings[1].toString()) != null)
                    Chat
                else
                    System
            } else if (message.siblings.isNotEmpty()) {
                for (text in message.siblings) {
                    if (text.style.clickEvent?.value?.startsWith("/switch ") == true) {
                        return Chat
                    }
                }
            }
            return System
        }

        fun getContent(message: Text): Text {
            return when (getType(message)) {
                Chat -> message.siblings.last()
                System -> message
            }
        }
    }
}