package io.github.nbcss.wynnlib.abilities.properties.general

import com.google.gson.JsonElement
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.builder.entries.PropertyEntry
import io.github.nbcss.wynnlib.abilities.properties.AbilityProperty
import io.github.nbcss.wynnlib.abilities.properties.ModifiableProperty
import io.github.nbcss.wynnlib.abilities.properties.SetupProperty
import io.github.nbcss.wynnlib.i18n.Translations
import io.github.nbcss.wynnlib.utils.Symbol
import io.github.nbcss.wynnlib.utils.removeDecimal
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class RangeProperty(ability: Ability, private val range: Double):
    AbilityProperty(ability), SetupProperty {
    companion object: Factory {
        override fun create(ability: Ability, data: JsonElement): AbilityProperty {
            return RangeProperty(ability, data.asDouble)
        }
        override fun getKey(): String = "range"
    }

    fun getRange(): Double = range

    override fun setup(entry: PropertyEntry) {
        entry.setProperty(getKey(), this)
    }

    override fun getTooltip(): List<Text> {
        val value = (if(range <= 1) Translations.TOOLTIP_SUFFIX_BLOCK else Translations.TOOLTIP_SUFFIX_BLOCKS)
            .formatted(Formatting.WHITE, null, removeDecimal(range))
        return listOf(Symbol.RANGE.asText().append(" ")
            .append(Translations.TOOLTIP_ABILITY_RANGE.formatted(Formatting.GRAY).append(": "))
            .append(value))
    }

    class Modifier(ability: Ability, data: JsonElement):
        AbilityProperty(ability), ModifiableProperty {
        companion object: Factory {
            override fun create(ability: Ability, data: JsonElement): AbilityProperty {
                return Modifier(ability, data)
            }
            override fun getKey(): String = "range_modifier"
        }
        private val modifier: Double = data.asDouble

        fun getRangeModifier(): Double = modifier

        override fun modify(entry: PropertyEntry) {
            entry.getProperty(RangeProperty.getKey())?.let {
                val range = (it as RangeProperty).getRange() + modifier
                entry.setProperty(RangeProperty.getKey(), RangeProperty(it.getAbility(), range))
            }
        }

        override fun getTooltip(): List<Text> {
            val color = if (modifier <= 0) Formatting.RED else Formatting.GREEN
            val value = (if(modifier <= 1) Translations.TOOLTIP_SUFFIX_BLOCK else Translations.TOOLTIP_SUFFIX_BLOCKS)
                .formatted(color, null, (if (modifier > 0) "+" else "") + removeDecimal(modifier))
            return listOf(Symbol.RANGE.asText().append(" ")
                .append(Translations.TOOLTIP_ABILITY_RANGE.formatted(Formatting.GRAY).append(": "))
                .append(value))
        }
    }
}