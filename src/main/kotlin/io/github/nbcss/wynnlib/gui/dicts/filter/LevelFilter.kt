package io.github.nbcss.wynnlib.gui.dicts.filter

import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.gui.widgets.scrollable.CheckboxWidget
import io.github.nbcss.wynnlib.gui.widgets.scrollable.ItemIconWidget
import io.github.nbcss.wynnlib.gui.widgets.scrollable.LabelWidget
import io.github.nbcss.wynnlib.items.equipments.Equipment
import io.github.nbcss.wynnlib.i18n.Translations.UI_FILTER_LEVEL
import io.github.nbcss.wynnlib.utils.ItemFactory
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.function.Supplier

class LevelFilter(memory: CriteriaState<Equipment>,
                     private val screen: TooltipScreen): FilterGroup<Equipment>(memory) {
    companion object {
        private const val FILTER_KEY = "COMBAT_LEVEL"
    }
    private val checkboxes: MutableMap<List<Int>, CheckboxWidget> = linkedMapOf()
    private val contentHeight: Int
    init {
        var index = 0
        val range = listOf(1, 42, 83)
        val level_ff = (memory.getFilter(FILTER_KEY) as? CombatLevelFilter)?.level
        addElement(LabelWidget(2, 2, Supplier {
            return@Supplier UI_FILTER_LEVEL.formatted(Formatting.GOLD)
        }, mode = LabelWidget.Mode.OUTLINE))
        val levels = mutableListOf<List<Int>>()
        (0..10).forEach {
            levels.add(((it*10)..(it*10)+9).toList())
        }
        // TODO(switch to a range slider)
        levels.forEach { level ->
            val posX = range[index % range.size]
            val posY = 12 + 20 * (index / range.size)
            val name = Text.of("%s..%s".format(level.first(), level.last()))
            val checkbox = CheckboxWidget(posX, posY, name, screen,
                level_ff?.containsAll(level) ?: true)
            checkbox.setCallback { updateFilter() }
            addElement(checkbox)
            addElement(ItemIconWidget(posX + 20, posY + 1, ItemFactory.fromEncoding("minecraft:iron_sword")))
            checkboxes[level] = checkbox
            index += 1
        }
        val group = CheckboxWidget.Group(checkboxes.values.toSet()) {
            updateFilter()
        }
        checkboxes.values.forEach { it.setGroup(group) }
        contentHeight = 10 + if (index % range.size == 0) {
            20 * (index / range.size)
        }else{
            20 * (1 + index / range.size)
        }
    }

    private fun updateFilter() {
        var levels = listOf<Int>()
        checkboxes.entries.forEach {
            if (it.value.isChecked()) {
                levels = levels + it.key
            }
        }
        memory.putFilter(CombatLevelFilter(levels.toSet()))
    }

    override fun getHeight(): Int {
        return contentHeight
    }

    override fun reload(memory: CriteriaState<Equipment>) {
        memory.getFilter(FILTER_KEY)?.let {
            if (it is CombatLevelFilter) {
                for (entry in checkboxes.entries) {
                    entry.value.setChecked(it.level.containsAll(entry.key))
                }
            }
        }
    }

    class CombatLevelFilter(val level: Set<Int>): CriteriaState.Filter<Equipment> {

        override fun accept(item: Equipment): Boolean {
            return item.getLevel().lower() in level
        }

        override fun getKey(): String = FILTER_KEY
    }
}