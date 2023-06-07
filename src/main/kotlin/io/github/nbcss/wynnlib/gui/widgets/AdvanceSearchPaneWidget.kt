package io.github.nbcss.wynnlib.gui.widgets

import io.github.nbcss.wynnlib.gui.DictionaryScreen
import io.github.nbcss.wynnlib.gui.dicts.filter.CriteriaState
import io.github.nbcss.wynnlib.gui.dicts.filter.CriteriaGroup
import io.github.nbcss.wynnlib.gui.dicts.filter.FilterGroup
import io.github.nbcss.wynnlib.gui.dicts.filter.GroupContainer
import io.github.nbcss.wynnlib.gui.widgets.buttons.ExitButtonWidget
import io.github.nbcss.wynnlib.gui.widgets.ListContainerScroll
import io.github.nbcss.wynnlib.i18n.Translations
import io.github.nbcss.wynnlib.items.BaseItem
import io.github.nbcss.wynnlib.render.RenderKit
import io.github.nbcss.wynnlib.render.TextureData
import io.github.nbcss.wynnlib.utils.Color
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class AdvanceSearchPaneWidget<T: BaseItem>(private val screen: DictionaryScreen<T>,
                                           private val criteriaList: List<CriteriaGroup<T>>,
                                           x: Int,
                                           y: Int): ClickableWidget(x, y, WIDTH, HEIGHT, null) {
    companion object {
        private val TEXTURE = Identifier("wynnlib", "textures/gui/search_filters.png")
        private val SLIDER_TEXTURE = TextureData(TEXTURE, 148)
        private val client = MinecraftClient.getInstance()
        const val WIDTH = 148
        const val HEIGHT = 182
        const val SCROLL_WIDTH = WIDTH - 19
    }
    private val close: ExitButtonWidget
    private val slider: VerticalSliderWidget
    private val scroll: Scroll
    init {
        close = ExitButtonWidget(x + width - 15, y + 3, object: ExitButtonWidget.ExitHandler{
            override fun exit() {
                screen.setFilterVisible(false)
            }
        })
        scroll = Scroll()
        slider = VerticalSliderWidget(x + width - 12, y + 17, 6, 155, 30,
            SLIDER_TEXTURE){
            scroll.setScrollPosition(it * scroll.getMaxPosition())
        }
    }

    fun reload(memory: CriteriaState<T>) {
        criteriaList.forEach { it.reload(memory) }
    }

    fun getCriteriaList(): List<CriteriaGroup<T>> = criteriaList

    //override fun appendNarrations(builder: NarrationMessageBuilder?) {
    //    appendDefaultNarrations(builder)
    //}

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        TODO("Not yet implemented")
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (close.mouseClicked(mouseX, mouseY, button))
            return true
        if (scroll.mouseClicked(mouseX, mouseY, button))
            return true
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scroll.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (scroll.mouseReleased(mouseX, mouseY, button))
            return true
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (scroll.mouseScrolled(mouseX, mouseY, amount))
            return true
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (scroll.charTyped(chr, modifiers))
            return true
        return super.charTyped(chr, modifiers)
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        TODO("Not yet implemented")
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        //screen.setFilterVisible(false)
        RenderKit.renderTexture(
            context, TEXTURE, x, y, 0, 0, width, height
        )
        context!!.drawText(client.textRenderer, Translations.UI_ADVANCE_SEARCH.translate(), x + 6, y + 4, Color.BLACK.code(), false)
        close.render(context, mouseX, mouseY, delta)
        slider.render(context, mouseX, mouseY, delta)
        scroll.render(context, mouseX, mouseY, delta)
    }

    inner class Scroll: ListContainerScroll(null, screen,
        x + 5, y + 15, SCROLL_WIDTH, height - 24,
        elements = getCriteriaList().map { GroupContainer(it) }.toMutableList()) {
        override fun getSlider(): VerticalSliderWidget = slider
        override fun setFocused(focused: Boolean) {
            TODO("Not yet implemented")
        }

        override fun isFocused(): Boolean {
            TODO("Not yet implemented")
        }
    }

    class Builder<T: BaseItem>(private val screen: DictionaryScreen<T>,
                               private val x: Int,
                               private val y: Int) {
        private val filters: MutableList<CriteriaGroup<T>> = mutableListOf()

        fun filter(filter: FilterGroup<T>): Builder<T> {
            filters.add(filter)
            return this
        }

        fun sorter(): Builder<T> {
            //todo
            return this
        }

        fun build(): AdvanceSearchPaneWidget<T> {
            //todo
            return AdvanceSearchPaneWidget(screen, filters, x, y)
        }
    }
}