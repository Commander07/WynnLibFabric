package io.github.nbcss.wynnlib.gui.widgets.scrollable

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class TextFieldWidget(textRenderer: TextRenderer?, private val posX: Int,
                      private val posY: Int, width: Int, height: Int) :
    TextFieldWidget(textRenderer, posX, posY, width, height, Text.empty()), ScrollElement {
    private var interactable: Boolean = true

    override fun updateState(x: Int, y: Int, active: Boolean) {
        this.x = posX + x
        this.y = posY + y
        this.interactable = active
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        context?.matrices?.translate(0.0,0.0,100.0)
        super.renderButton(context, mouseX, mouseY, delta)
    }
}