package io.github.nbcss.wynnlib.gui.widgets.buttons

import com.mojang.blaze3d.systems.RenderSystem
import io.github.nbcss.wynnlib.render.RenderKit
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ExitButtonWidget(x: Int, y: Int, private val handler: ExitHandler):
    PressableWidget(x, y, 10, 10, Text.empty()) {

    private val texture: Identifier = Identifier("wynnlib", "textures/gui/exit_button.png")
    //override fun appendNarrations(builder: NarrationMessageBuilder?) {
    //    appendDefaultNarrations(builder)
    //}

    override fun onPress() {
        handler.exit()
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.enableDepthTest()
        val v = if (isHovered) 10 else 0
        RenderKit.renderTexture(context, texture, x, y, 0, v, 10, 10, 10, 20)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        // todo
    }

    interface ExitHandler {
        fun exit()
    }
}