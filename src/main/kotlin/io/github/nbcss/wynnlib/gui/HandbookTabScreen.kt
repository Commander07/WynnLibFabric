package io.github.nbcss.wynnlib.gui

import com.mojang.blaze3d.systems.RenderSystem
import io.github.nbcss.wynnlib.Settings
import io.github.nbcss.wynnlib.gui.widgets.buttons.ExitButtonWidget
import io.github.nbcss.wynnlib.render.RenderKit
import io.github.nbcss.wynnlib.render.TextureData
import io.github.nbcss.wynnlib.utils.playSound
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class HandbookTabScreen(val parent: Screen?,
                                 title: Text?) : Screen(title),
    TooltipScreen, ExitButtonWidget.ExitHandler {
    private val background = Identifier("wynnlib", "textures/gui/handbook_tab.png")
    companion object {
        const val TAB_SIZE: Int = 7
    }
    protected val backgroundWidth = 246
    protected val backgroundHeight = 210
    protected val tabs: MutableList<TabFactory> = ArrayList()
    private var tooltip: TooltipItem? = null
    private var tabPage: Int = 0
    protected var exitButton: ExitButtonWidget? = null
    protected var windowWidth = backgroundWidth
    protected var windowHeight = backgroundHeight
    protected var windowX = backgroundWidth
    protected var windowY = backgroundHeight
    init {
        //setup default tabs
        tabs.addAll(Settings.getHandbookTabs())
    }

    override fun init() {
        super.init()
        tabs.removeIf { !it.shouldDisplay() }
        clearChildren()
        windowWidth = backgroundWidth
        windowHeight = backgroundHeight
        windowX = (width - windowWidth) / 2
        windowY = (height - windowHeight) / 2
        val closeX = windowX + 230
        val closeY = windowY + 31
        exitButton = addDrawableChild(ExitButtonWidget(closeX, closeY, this))
    }

    open fun drawBackgroundPre(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val tabIndex = tabPage * TAB_SIZE
        (0 until TAB_SIZE).filter{tabIndex + it < tabs.size}
            .filter{!tabs[tabIndex + it].isInstance(this)}
            .forEach { drawTab(context!!, tabs[tabIndex + it], it, mouseX, mouseY) }
    }

    open fun drawBackgroundPost(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val tabIndex = tabPage * TAB_SIZE
        (0 until TAB_SIZE).filter{tabIndex + it < tabs.size}
            .filter{tabs[tabIndex + it].isInstance(this)}
            .forEach { drawTab(context!!, tabs[tabIndex + it], it, mouseX, mouseY) }
    }

    open fun drawBackgroundTexture(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderKit.renderTexture(
            context, background, windowX, windowY + 28, 0, 0,
            backgroundWidth, 182
        )
    }

    open fun drawBackground(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        drawBackgroundPre(context, mouseX, mouseY, delta)
        //render background
        drawBackgroundTexture(context, mouseX, mouseY, delta)
        //render selected tab (normally should only have up to one tab)
        drawBackgroundPost(context, mouseX, mouseY, delta)
        context?.drawText(textRenderer, getTitle().asOrderedText(), windowX + 6, windowY + 33, 0x000000, false)
        context?.setShaderColor(1f, 1f, 1f, 0f)
    }

    private fun drawTab(context: DrawContext, tab: TabFactory, tabIndex: Int, mouseX: Int, mouseY: Int) {
        val posX = windowX + 25 + tabIndex * 28
        val u = if (tab.isInstance(this)) 0 else 28
        RenderKit.renderTexture(context, background, posX, windowY, u, 182, 28, 32)
        context.drawItem(tab.getTabIcon(), posX + 6, windowY + 9)
        context.drawItemInSlot(textRenderer, tab.getTabIcon(), posX + 6, windowY + 9)
        if(isOverTab(tabIndex, mouseX, mouseY)){
            drawTooltip(context, tab.getTabTooltip(), mouseX, mouseY)
        }
    }

    private fun isOverTab(tabIndex: Int, mouseX: Int, mouseY: Int): Boolean {
        val posX = windowX + 25 + tabIndex * 28
        return mouseX >= posX && mouseX < posX + 28 && mouseY >= windowY && mouseY <= windowY + 28
    }

    abstract fun drawContents(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float)

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true
        }else if (this.client!!.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            client!!.setScreen(parent)
            return true
        }
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val tabIndex = tabPage * TAB_SIZE
        (0 until TAB_SIZE).filter{tabIndex + it < tabs.size}
            .firstOrNull {isOverTab(it, mouseX.toInt(), mouseY.toInt())}?.let {
                val tab = tabs[tabIndex + it]
                client!!.setScreen(tab.createScreen(parent))
                playSound(SoundEvents.ITEM_BOOK_PAGE_TURN)
                return true
            }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        drawBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        drawContents(context, mouseX, mouseY, delta)
        tooltip?.let { item ->
            context?.drawOrderedTooltip(this.textRenderer, item.tooltip.map{ it.asOrderedText()}, item.x, item.y)
            RenderSystem.enableDepthTest()
            tooltip = null
        }
    }

    override fun shouldPause(): Boolean = false

    override fun exit() {
        client!!.setScreen(parent)
    }

    override fun drawTooltip(context: DrawContext, tooltip: List<Text>, x: Int, y: Int) {
        this.tooltip = TooltipItem(x, y, tooltip)
        /*matrices.push()
        renderOrderedTooltip(matrices, tooltip.map{it.asOrderedText()}, x, y)
        RenderSystem.enableDepthTest()
        matrices.pop()*/
    }

    private data class TooltipItem(val x: Int, val y: Int, val tooltip: List<Text>)
}