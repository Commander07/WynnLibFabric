package io.github.nbcss.wynnlib

import io.github.nbcss.wynnlib.gui.dicts.EquipmentDictScreen
import io.github.nbcss.wynnlib.utils.keys.UnregisteredKeyBinding
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW

object WynnLibKeybindings {
    private val lockSlot = registerKey("wynnlib.key.slot_lock", GLFW.GLFW_KEY_L, true)
    private val openHandbook = registerKey("wynnlib.key.handbook", GLFW.GLFW_KEY_H)
    private val toggleAnalyze = registerKey("wynnlib.key.analyze_mode", GLFW.GLFW_KEY_LEFT_CONTROL, true)
    private val openContentBook = registerKey("wynnlib.key.content_book", GLFW.GLFW_KEY_GRAVE_ACCENT)

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (openHandbook.wasPressed()) {
                it.setScreen(EquipmentDictScreen(it.currentScreen))
            }
            while (openContentBook.wasPressed()) {
                it.player?.currentScreenHandler?.syncId?.let { syncId -> it.interactionManager?.clickSlot(syncId, 43, 1, SlotActionType.PICKUP, it.player) }
            }
        })
    }

    fun getLockSlotKeybinding(): KeyBinding = lockSlot

    fun getToggleAnalyzeKeybinding(): KeyBinding = toggleAnalyze

    private fun registerKey(name: String, key: Int, unregister: Boolean = false): KeyBinding {
        val keyBinding = if(unregister) {
            UnregisteredKeyBinding(name, "wynnlib.category.keys", key)
        }else{
            KeyBinding(name, InputUtil.Type.KEYSYM, key, "wynnlib.category.keys")
        }
        return KeyBindingHelper.registerKeyBinding(keyBinding)
    }
}