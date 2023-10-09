package io.github.nbcss.wynnlib.analysis.properties

import net.minecraft.text.Text

class PriceProperty: AnalysisProperty {
    companion object {
        const val KEY = "PRICE"
    }
    private var priceTooltip: MutableList<Text>? = null

    fun getPriceTooltip(): List<Text>? = priceTooltip

    override fun set(tooltip: List<Text>, line: Int): Int {
        if (priceTooltip != null)
            return 0
        if (tooltip[line].string.isNotEmpty() || tooltip[line].siblings.isEmpty())
            return 0
        val siblings = tooltip[line].siblings
        if (siblings[0].string == "Price:" && siblings[0].style.color?.name == "gold") {
            var i = 1
            val priceTooltip: MutableList<Text> = mutableListOf()
            priceTooltip.add(tooltip[line])
            while (line + i < tooltip.size) {
                val text1 = tooltip[line + i]
                if (text1.string.isNotEmpty() || text1.siblings.isEmpty())
                    break
                val text2 = text1.siblings[0]
                if (text2.string.isNotEmpty() || text2.siblings.isEmpty())
                    break
                val text3 = text2.siblings[0]
                if (text3.string == " - " && text3.style.color?.name == "gold") {
                    priceTooltip.add(text1)
                }else{
                    break
                }
                i++
            }
            this.priceTooltip = priceTooltip
            return i
        }
        return 0
    }

    override fun getKey(): String = KEY
}