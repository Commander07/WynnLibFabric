package io.github.nbcss.wynnlib.gui.widgets.scrollable

import io.github.nbcss.wynnlib.render.RenderKit
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import io.github.nbcss.wynnlib.utils.Color
import net.minecraft.client.gui.DrawContext
import java.util.function.Supplier

class LabelWidget(posX: Int, posY: Int,
                  private val textProvider: Supplier<Text?>,
                  private val colorProvider: Supplier<Color?>? = null,
                  private val mode: Mode = Mode.SHADOW): BaseScrollableWidget(posX, posY) {
    companion object {
        private val renderer = MinecraftClient.getInstance().textRenderer
    }

    override fun setFocused(focused: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isFocused(): Boolean {
        TODO("Not yet implemented")
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        textProvider.get()?.let {
            val color = colorProvider?.get() ?: Color.WHITE
            when (mode) {
                Mode.PLAIN -> {
                    context!!.drawText(renderer, it, getX(), getY(), color.code(), false)
                }
                Mode.SHADOW -> {
                    context!!.drawTextWithShadow(renderer, it, getX(), getY(), color.code())
                }
                Mode.OUTLINE -> {
                    RenderKit.renderOutlineText(context!!.matrices, it, getX().toFloat(), getY().toFloat(), color)
                }
            }
        }
    }

    enum class Mode {
        PLAIN,
        SHADOW,
        OUTLINE
    }
}