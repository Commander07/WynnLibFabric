package io.github.nbcss.wynnlib.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.nbcss.wynnlib.utils.AlphaColor
import io.github.nbcss.wynnlib.utils.Color
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

object RenderKit {
    private val textRender = MinecraftClient.getInstance().textRenderer
    fun renderTexture(context: DrawContext?,
                      texture: Identifier,
                      x: Int,
                      y: Int,
                      u: Int,
                      v: Int,
                      width: Int,
                      height: Int) {
        renderTexture(context, texture, x, y, u, v, width, height, 256, 256)
    }

    fun renderTexture(context: DrawContext,
                      texture: Identifier,
                      x: Double,
                      y: Double,
                      u: Int,
                      v: Int,
                      width: Int,
                      height: Int,
                      texWidth: Int,
                      texHeight: Int) {
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(texture, x.toInt(), y.toInt(), u.toFloat(), v.toFloat(), width, height, texWidth, texHeight)
    }

    fun renderTexture(context: DrawContext?,
                      texture: Identifier,
                      x: Int,
                      y: Int,
                      u: Int,
                      v: Int,
                      width: Int,
                      height: Int,
                      texWidth: Int,
                      texHeight: Int) {
        context?.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context?.drawTexture(texture, x, y, u.toFloat(), v.toFloat(), width, height, texWidth, texHeight)
    }

    fun renderAnimatedTexture(context: DrawContext,
                              texture: Identifier,
                              x: Int,
                              y: Int,
                              width: Int,
                              height: Int,
                              frames: Int,
                              intervalTime: Long = 50,
                              slackTime: Long = 0) {
        val duration = frames * intervalTime + slackTime
        val time = System.currentTimeMillis() % duration
        val index = min((time / intervalTime).toInt(), frames - 1)
        val v = (index * height).toFloat()
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(texture, x, y, 0.0f, v, width, height, width, frames * height)
    }

    fun renderTextureWithColor(context: DrawContext,
                               texture: Identifier,
                               color: AlphaColor,
                               x: Int,
                               y: Int,
                               u: Int,
                               v: Int,
                               width: Int,
                               height: Int,
                               texWidth: Int,
                               texHeight: Int) {
        RenderSystem.enableBlend()
        context.setShaderColor(color.floatRed(), color.floatGreen(), color.floatBlue(), color.floatAlpha())
        context.drawTexture(texture, x, y, u.toFloat(), v.toFloat(), width, height, texWidth, texHeight)
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun renderOutlineText(matrices: MatrixStack, text: String, x: Float, y: Float,
                          color: Color = Color.WHITE,
                          outlineColor: Color = Color.BLACK){
        renderOutlineText(matrices, Text.literal(text), x, y, color, outlineColor)
    }

    fun renderDefaultOutlineText(matrices: MatrixStack, text: Text, x: Float, y: Float) {
        renderOutlineText(matrices, text, x, y)
    }

    fun renderOutlineText(matrices: MatrixStack, text: Text, x: Float, y: Float,
                          color: Color = Color.WHITE,
                          outlineColor: Color = Color.BLACK) {
        val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
        textRender.drawWithOutline(text.asOrderedText(), x, y,
            color.code(), outlineColor.code(),
            matrices.peek().positionMatrix, immediate, 15728880)
        immediate.draw()
    }

    fun renderItemBar(context: DrawContext, progress: Double, color: Int, x: Int, y: Int, z: Int) {
        val k = x + 2
        val l = y + 13
        context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, z, -16777216)
        context.fill(RenderLayer.getGuiOverlay(), k, l, k + (progress * 13).toInt(), l + 1, z, color or -16777216)
    }

    fun renderWayPointText(
        context: DrawContext,
        texts: List<Text>,
        worldX: Double,
        worldY: Double,
        worldZ: Double,
        showDistance: Boolean,
        backgroundAlpha: Int = 0x55) {
        val client = MinecraftClient.getInstance()
        val camera = client.gameRenderer.camera
        val dx = worldX - camera.pos.x
        val dy = worldY - camera.pos.y
        val dz = worldZ - camera.pos.z
        val distance = sqrt((dx * dx + dy * dy + dz * dz)).toFloat()
        var zoom = 1.0f
        val radius = 10
        if (distance > radius) {
            zoom = distance / radius
        }
        RenderSystem.disableDepthTest()
        val matrices = context.matrices
        matrices.push()
        matrices.loadIdentity()
        matrices.translate(dx, dy, dz)
        matrices.multiply(camera.rotation)
        matrices.scale(-0.025f, -0.025f, 0.025f)
        matrices.scale(zoom, zoom, 1.0f)
        val width = texts.maxOf { textRender.getWidth(it) }
        val textX = -width / 2.0f
        var textY = -(texts.size + if (showDistance) 1 else 0) * 10 / 2.0f
        val consumerProvider = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
        val matrix4f = matrices.peek().positionMatrix
        context.fill(textX.toInt() - 2,
            textY.toInt() - 1,
            textX.toInt() + width + 2,
            textY.toInt() + texts.size * 10 - 1,
            Color.BLACK.withAlpha(backgroundAlpha).code())
        for (text in texts) {
            textRender.draw(text, textX, textY, 0xFFFFFF, false,
                matrix4f, consumerProvider, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
            textY += 10.0f
        }
        if (showDistance){
            RenderSystem.disableDepthTest()
            val distText = Text.literal("${distance.roundToInt()}m")
            val distX = (-textRender.getWidth(distText) / 2).toFloat()
            /*textRender.drawWithOutline(distText.asOrderedText(), distX, textY, 0xFFFFFF, 0,
                    matrix4f, consumerProvider, 255)*/
            textRender.draw(distText, distX, textY, 0xFFFFFF, false,
                matrix4f, consumerProvider, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
        }
        consumerProvider.draw()
        matrices.pop()
        RenderSystem.enableDepthTest()
    }
}