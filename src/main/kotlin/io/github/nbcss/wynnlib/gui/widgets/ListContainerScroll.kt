package io.github.nbcss.wynnlib.gui.widgets

import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.gui.widgets.scrollable.ScrollListEntry
import io.github.nbcss.wynnlib.render.TextureData
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack

open class ListContainerScroll(background: TextureData?,
                          screen: TooltipScreen,
                          x: Int,
                          y: Int,
                          width: Int,
                          height: Int,
                          private var elements: MutableList<ScrollListEntry> = mutableListOf(),
                          scrollDelay: Long = 200L,
                          scrollUnit: Double = 32.0):
    AbstractElementScroll(background, screen, x, y, width, height, scrollDelay, scrollUnit) {

    fun addElement(element: ScrollListEntry) {
        elements.add(element)
    }

    override fun getContentHeight(): Int {
        return elements.sumOf { it.getEntryHeight() }
    }

    override fun setFocused(focused: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isFocused(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getElements(): List<Element> = elements

    override fun renderContents(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        position: Double,
        delta: Float,
        mouseOver: Boolean
    ) {
        val posX = x
        val posY = (y - position).toInt()
        var top = 0
        elements.forEach {
            it.updateTop(top)
            it.updateState(posX, posY, mouseOver)
            it.render(context, mouseX, mouseY, delta)
            top += it.getEntryHeight()
        }
    }
}