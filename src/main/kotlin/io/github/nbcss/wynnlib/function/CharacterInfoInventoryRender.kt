package io.github.nbcss.wynnlib.function

import io.github.nbcss.wynnlib.data.CharacterProfile
import io.github.nbcss.wynnlib.events.EventHandler
import io.github.nbcss.wynnlib.events.InventoryRenderEvent
import io.github.nbcss.wynnlib.render.RenderKit
import io.github.nbcss.wynnlib.utils.Color
import io.github.nbcss.wynnlib.utils.WynnValues
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object CharacterInfoInventoryRender: EventHandler<InventoryRenderEvent> {
    private val client: MinecraftClient = MinecraftClient.getInstance()
    override fun handle(event: InventoryRenderEvent) {
        if (event.phase != InventoryRenderEvent.Phase.POST)
            return
        val title = event.screen.title.toString()
        if (title != "Character Info")
            return
        val handler = event.screen.screenHandler
        if (handler.slots.size < 63)
            return
        val abilityTreeItem = handler.getSlot(9).stack
        val ap = if (abilityTreeItem.isEmpty) "-" else
            abilityTreeItem.getTooltip(client.player, TooltipContext.Default.BASIC)
                .asSequence()
                .filter { it.siblings.isNotEmpty() }
                .map { it.siblings[0] }
                .filter { it.siblings.size >= 2 }
                .filter { it.siblings[0].toString().contains("Unused Points: ") }
                .filter { it.siblings[1].toString().matches("\\d+".toRegex()) }
                .map { it.siblings[1].toString().toInt() }
                .firstOrNull() ?: "-"
        val level = client.player?.experienceLevel ?: 0
        val maxAp = WynnValues.getMaxAP(level)
        val skillPointItem = handler.getSlot(4).stack
        val sp = if (skillPointItem.isEmpty) "-" else
            skillPointItem.getTooltip(client.player, TooltipContext.Default.BASIC)
                .asSequence()
                .filter { it.siblings.isNotEmpty() }
                .map { it.siblings[0] }
                .filter { it.siblings.size >= 2 }
                .filter { it.siblings[0].toString() == "You have " }
                .filter { it.siblings[1].toString().matches("\\d+".toRegex()) }
                .map { it.siblings[1].toString().toInt() }
                .firstOrNull() ?: "-"
        val maxSp = WynnValues.getMaxSP(level)
        val posX = event.screenX.toFloat()
        val posY = event.screenY.toFloat() + 2
        val apText = Text.literal("✦ $ap/$maxAp").formatted(Formatting.DARK_AQUA)
        val spText = Text.literal("$sp/$maxSp ✦").formatted(Formatting.GREEN)
        event.matrices.push()
        event.matrices.translate(0.0, 0.0, 200.0)
        //client.textRenderer.draw(event.matrices, text, posX, posY, 0xFFFFFF)
        //client.textRenderer.drawWithShadow(event.matrices, text, posX, posY, Color.DARK_AQUA.code())
        RenderKit.renderOutlineText(event.matrices, apText, posX, posY, Color.WHITE)
        val posX2 = posX + 174 - client.textRenderer.getWidth(spText)
        RenderKit.renderOutlineText(event.matrices, spText, posX2, posY, Color.WHITE)
        event.matrices.pop()
    }
}