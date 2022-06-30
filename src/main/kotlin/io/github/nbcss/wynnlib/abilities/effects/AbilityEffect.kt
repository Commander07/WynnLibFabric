package io.github.nbcss.wynnlib.abilities.effects

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.effects.spells.warrior.*
import io.github.nbcss.wynnlib.abilities.effects.spells.SpellUnlock
import io.github.nbcss.wynnlib.abilities.effects.spells.archer.*
import net.minecraft.text.Text

interface AbilityEffect {
    companion object {
        private val FACTORY_MAP: Map<String, Factory> = mapOf(
            //Warrior Ability Effects
            "BASH" to BashSpell,
            "CHARGE" to ChargeSpell,
            "UPPERCUT" to UppercutSpell,
            "WAR_SCREAM" to WarScreamSpell,
            "CHEAPER_BASH_I" to CostModifier,
            "CHEAPER_CHARGE" to CostModifier,
            "CHEAPER_UPPERCUT_I" to CostModifier,
            "CHEAPER_BASH_II" to CostModifier,
            "CHEAPER_WAR_SCREAM" to CostModifier,
            "CHEAPER_UPPERCUT_II" to CostModifier,
            "WARRIOR_AIR_MASTERY" to ElementMastery,
            "WARRIOR_FIRE_MASTERY" to ElementMastery,
            "WARRIOR_WATER_MASTERY" to ElementMastery,
            "WARRIOR_THUNDER_MASTERY" to ElementMastery,
            "WARRIOR_EARTH_MASTERY" to ElementMastery,
            //Archer Ability Effects
            "ARROW_STORM" to ArrowStormSpell,
            "ESCAPE" to EscapeSpell,
            "ARROW_BOMB" to ArrowBombSpell,
            "ARROW_SHIELD" to ArrowShieldSpell,
            "CHEAPER_ARROW_BOMB_I" to CostModifier,
            "CHEAPER_ESCAPE_I" to CostModifier,
            "CHEAPER_ARROW_STORM_I" to CostModifier,
            "CHEAPER_ARROW_STORM_II" to CostModifier,
            "CHEAPER_ARROW_SHIELD" to CostModifier,
            "CHEAPER_ESCAPE_II" to CostModifier,
            "CHEAPER_ARROW_BOMB_II" to CostModifier,
            "ARCHER_AIR_MASTERY" to ElementMastery,
            "ARCHER_FIRE_MASTERY" to ElementMastery,
            "ARCHER_WATER_MASTERY" to ElementMastery,
            "ARCHER_THUNDER_MASTERY" to ElementMastery,
            "ARCHER_EARTH_MASTERY" to ElementMastery,
        )

        fun fromData(id: String, properties: JsonObject): AbilityEffect {
            return FACTORY_MAP.getOrDefault(id.uppercase(), BaseEffect.FACTORY).create(properties)
        }
    }

    /**
     * Get the effect items tooltip of the ability.
     *
     * @return effect tooltip, could be empty.
     */
    fun getEffectTooltip(): List<Text>

    /**
     * Get the property string for given key.
     * If the key does not exist, same key will be returned.
     *
     * @param key the key of the string
     * @return property string of given key, or the key itself if no associated property.
     */
    fun getPropertyString(key: String): String

    interface Factory {
        fun create(properties: JsonObject): AbilityEffect
    }
}