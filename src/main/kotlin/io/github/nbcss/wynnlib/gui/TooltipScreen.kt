package io.github.nbcss.wynnlib.gui

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

interface TooltipScreen {
    fun drawTooltip(context: DrawContext, tooltip: List<Text>, x: Int, y: Int)
}