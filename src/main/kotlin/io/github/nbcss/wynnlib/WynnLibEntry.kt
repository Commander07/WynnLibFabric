package io.github.nbcss.wynnlib

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.IconTexture
import io.github.nbcss.wynnlib.data.Identification
import io.github.nbcss.wynnlib.data.MajorId
import io.github.nbcss.wynnlib.data.PowderSpecial
import io.github.nbcss.wynnlib.events.ClientTickEvent
import io.github.nbcss.wynnlib.events.EventRegistry
import io.github.nbcss.wynnlib.events.RenderWorldEvent
import io.github.nbcss.wynnlib.items.identity.ConfigurableItem
import io.github.nbcss.wynnlib.registry.*
import io.github.nbcss.wynnlib.timer.status.StatusType
import io.github.nbcss.wynnlib.utils.FileUtils
import io.github.nbcss.wynnlib.utils.Scheduler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.system.exitProcess


@Suppress("UNUSED")
object WynnLibEntry: ModInitializer {
    private const val MOD_ID = "wynnlib"

    override fun onInitialize() {
        // Auto update
        val gson = Gson()
        val httpClient = HttpClient.newBuilder().build()
        val wynntils = gson.fromJson(httpClient.send(HttpRequest.newBuilder().uri(URI.create("https://raw.githubusercontent.com/Wynntils/WynntilsWebsite-API/master/urls.json")).build(), HttpResponse.BodyHandlers.ofString()).body(), JsonArray::class.java)
        var ingEndpoint: JsonObject? = null
        var gearEndpoint: JsonObject? = null
        for (endpoint in wynntils.asJsonArray.asList().drop(1)) {
            if (endpoint.asJsonObject["id"].asString == "dataStaticIngredients") {
                ingEndpoint = endpoint.asJsonObject
            } else if (endpoint.asJsonObject["id"].asString == "dataStaticGear") {
                gearEndpoint = endpoint.asJsonObject
            }
        }
        if (ingEndpoint == null) {
            error("ing_endpoint is null")
        } else if (gearEndpoint == null) {
            error("gear_endpoint is null")
        }
        // Gear
        val gear_current = FileUtils.readFile("config/WynnLib/Equipments.json")
        if ((gear_current == null) || (gearEndpoint["md5"] != gear_current["md5"])) {
            val itemsJson = gson.fromJson(httpClient.send(HttpRequest.newBuilder().uri(URI.create(gearEndpoint["url"].asString)).build(), HttpResponse.BodyHandlers.ofString()).body(), JsonObject::class.java)
            itemsJson.addProperty("version", "0.2.10")
            itemsJson.add("data", itemsJson["items"])
            itemsJson.remove("items")
            itemsJson.addProperty("md5", gearEndpoint["md5"].asString)

            // Yes this is terribly inefficient but it will only be ran on install or api update which means it will run at most once a month
            // It will also increase file size but i doubt that will be an issue + i dont want to add checks for all fields to avoid a crash with missing keys
            val equDefFields = gson.fromJson(FileUtils.getResource("assets/wynnlib/data/EquipmentDefaultFields.json")?.reader(), JsonObject::class.java)
            for (i in itemsJson["data"].asJsonArray.asList().indices) {
                val equ = itemsJson["data"].asJsonArray[i].asJsonObject
                for (key in equDefFields[equ["category"].asString].asJsonObject.keySet()) {
                    if (!equ.has(key)) {
                        equ.add(key, equDefFields[equ["category"].asString].asJsonObject[key])
                    }
                }
            }

            FileUtils.writeFile("config/WynnLib/Equipments.json", itemsJson)
//            FileUtils.writeFile(FileUtils.getResourceURL("assets/wynnlib/data/Equipments.json").toString().replace("file:/", ""), itemsJson)
        }
        // Ings
        val ings_current = FileUtils.readFile("config/WynnLib/Ingredients.json")
        if ((ings_current == null) || (ingEndpoint["md5"] != ings_current["md5"])) {
            val ingsJson = gson.fromJson(httpClient.send(HttpRequest.newBuilder().uri(URI.create(ingEndpoint["url"].asString)).build(), HttpResponse.BodyHandlers.ofString()).body(), JsonObject::class.java)
            ingsJson.addProperty("version", "0.2.10")
            ingsJson.add("data", ingsJson["ingredients"])
            ingsJson.remove("ingredients")
            ingsJson.addProperty("md5", ingEndpoint["md5"].asString)

            // Yes this is terribly inefficient but it will only be ran on install or api update which means it will run at most once a month
            // It will also increase file size but i doubt that will be an issue + i dont want to add checks for all fields to avoid a crash with missing keys
            val ingDefFields = gson.fromJson(FileUtils.getResource("assets/wynnlib/data/IngredientDefaultFields.json")?.reader(), JsonObject::class.java)
            for (i in ingsJson["data"].asJsonArray.asList().indices) {
                val ing = ingsJson["data"].asJsonArray[i].asJsonObject
                for (key in ingDefFields.keySet()) {
                    if (!ing.has(key)) {
                        ing.add(key, ingDefFields[key])
                    }
                }
            }

            FileUtils.writeFile("config/WynnLib/Ingredients.json", ingsJson)
//            FileUtils.writeFile(FileUtils.getResourceURL("assets/wynnlib/data/Ingredients.json").toString().replace("file:/", ""), ingsJson)
        }

        //Reload Settings & auto saving
        Settings.reload()
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            Scheduler.tick()
            ClientTickEvent.handleEvent(ClientTickEvent())
        })
        //Reload icons
        IconTexture.reload()
        //Load data
        AbilityRegistry.load()
        Identification.load() //id have to load after ability, because spell id need ability name from abilities...
        StatusType.load()
        MajorId.load()
        PowderSpecial.load()
        PowderRegistry.load()
        RegularEquipmentRegistry.load()
        IngredientRegistry.load()
        MaterialRegistry.load()
        RecipeRegistry.load()
        TomeRegistry.load()
        CharmRegistry.load()
        //Load local user info
        AbilityBuildStorage.load()
        ConfigurableItem.Modifier.load()
        //Register keybindings
        WynnLibKeybindings.init()
        //Register events
        EventRegistry.registerEvents()
        WorldRenderEvents.LAST.register {
            RenderWorldEvent.handleEvent(RenderWorldEvent(it))
        }
    }
}