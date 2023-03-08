package io.github.nbcss.wynnlib.abilities.properties.assassin

import com.google.gson.JsonElement
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.PlaceholderContainer
import io.github.nbcss.wynnlib.abilities.PropertyProvider
import io.github.nbcss.wynnlib.abilities.builder.entries.PropertyEntry
import io.github.nbcss.wynnlib.abilities.properties.AbilityProperty
import io.github.nbcss.wynnlib.abilities.properties.ModifiableProperty
import io.github.nbcss.wynnlib.abilities.properties.SetupProperty
import io.github.nbcss.wynnlib.i18n.Translations
import io.github.nbcss.wynnlib.utils.Symbol
import io.github.nbcss.wynnlib.utils.colorOf
import io.github.nbcss.wynnlib.utils.signed
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class SmokeBombProperty(ability: Ability,
                        private val bombs: Int):
    AbilityProperty(ability), SetupProperty {
    companion object: Type<SmokeBombProperty> {
        override fun create(ability: Ability, data: JsonElement): SmokeBombProperty {
            return SmokeBombProperty(ability, data.asInt)
        }
        override fun getKey(): String = "smoke_bombs"
    }

    fun getSmokeBombs(): Int = bombs

    override fun writePlaceholder(container: PlaceholderContainer) {
        container.putPlaceholder(getKey(), bombs.toString())
    }

    override fun setup(entry: PropertyEntry) {
        entry.setProperty(getKey(), this)
    }

    override fun getTooltip(provider: PropertyProvider): List<Text> {
        return listOf(Symbol.ALTER_HITS.asText().append(" ")
            .append(Translations.TOOLTIP_ABILITY_ASSASSIN_SMOKE_BOMBS.formatted(Formatting.GRAY).append(": "))
            .append(Text.literal(bombs.toString()).formatted(Formatting.WHITE)))
    }

    class Modifier(ability: Ability,
                   private val modifier: Int):
        AbilityProperty(ability), ModifiableProperty {
        companion object: Type<Modifier> {
            override fun create(ability: Ability, data: JsonElement): Modifier {
                return Modifier(ability, data.asInt)
            }
            override fun getKey(): String = "smoke_bombs_modifier"
        }

        fun getModifier(): Int = modifier

        override fun writePlaceholder(container: PlaceholderContainer) {
            container.putPlaceholder(getKey(), modifier.toString())
        }

        override fun modify(entry: PropertyEntry) {
            SmokeBombProperty.from(entry)?.let {
                val bombs = it.getSmokeBombs() + getModifier()
                entry.setProperty(SmokeBombProperty.getKey(), SmokeBombProperty(it.getAbility(), bombs))
            }
        }

        override fun getTooltip(provider: PropertyProvider): List<Text> {
            return listOf(Symbol.ALTER_HITS.asText().append(" ")
                .append(Translations.TOOLTIP_ABILITY_ASSASSIN_SMOKE_BOMBS.formatted(Formatting.GRAY).append(": "))
                .append(Text.literal(signed(modifier)).formatted(colorOf(modifier))))
        }
    }
}