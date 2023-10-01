package io.github.nbcss.wynnlib.function

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.utils.FileUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object AutoUpdater {
    private const val endpoints = "https://raw.githubusercontent.com/Wynntils/WynntilsWebsite-API/master/urls.json"
    private val gson = Gson()
    private val httpClient = HttpClient.newHttpClient()

    fun update(): Boolean {
        try {
            val wynntils = gson.fromJson(
                httpClient.send(
                    HttpRequest.newBuilder()
                        .uri(URI.create(endpoints))
                        .build(), HttpResponse.BodyHandlers.ofString()
                ).body(), JsonArray::class.java
            )
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
                error("ingEndpoint is null")
            } else if (gearEndpoint == null) {
                error("gearEndpoint is null")
            }
            // Gear
            val gear_current = FileUtils.readFile("config/WynnLib/Equipments.json")
            if ((gear_current == null) || (gearEndpoint["md5"] != gear_current["md5"])) {
                val itemsJson = gson.fromJson(
                    httpClient.send(
                        HttpRequest.newBuilder().uri(URI.create(gearEndpoint["url"].asString)).build(),
                        HttpResponse.BodyHandlers.ofString()
                    ).body(), JsonObject::class.java
                )
                itemsJson.addProperty("version", "0.2.10")
                itemsJson.add("data", itemsJson["items"])
                itemsJson.remove("items")
                itemsJson.addProperty("md5", gearEndpoint["md5"].asString)

                // Yes this is terribly inefficient but it will only be ran on install or api update which means it will run at most once a month
                // It will also increase file size but i doubt that will be an issue + i dont want to add checks for all fields to avoid a crash with missing keys
                val equDefFields = gson.fromJson(
                    FileUtils.getResource("assets/wynnlib/data/EquipmentDefaultFields.json")?.reader(),
                    JsonObject::class.java
                )
                for (i in itemsJson["data"].asJsonArray.asList().indices) {
                    val equ = itemsJson["data"].asJsonArray[i].asJsonObject
                    for (key in equDefFields[equ["category"].asString].asJsonObject.keySet()) {
                        if (!equ.has(key)) {
                            equ.add(key, equDefFields[equ["category"].asString].asJsonObject[key])
                        }
                    }
                }

                FileUtils.writeFile("config/WynnLib/Equipments.json", itemsJson)
            }
            // Ings
            val ings_current = FileUtils.readFile("config/WynnLib/Ingredients.json")
            if ((ings_current == null) || (ingEndpoint["md5"] != ings_current["md5"])) {
                val ingsJson = gson.fromJson(
                    httpClient.send(
                        HttpRequest.newBuilder().uri(URI.create(ingEndpoint["url"].asString)).build(),
                        HttpResponse.BodyHandlers.ofString()
                    ).body(), JsonObject::class.java
                )
                ingsJson.addProperty("version", "0.2.10")
                ingsJson.add("data", ingsJson["ingredients"])
                ingsJson.remove("ingredients")
                ingsJson.addProperty("md5", ingEndpoint["md5"].asString)

                // Yes this is terribly inefficient but it will only be ran on install or api update which means it will run at most once a month
                // It will also increase file size but i doubt that will be an issue + i dont want to add checks for all fields to avoid a crash with missing keys
                val ingDefFields = gson.fromJson(
                    FileUtils.getResource("assets/wynnlib/data/IngredientDefaultFields.json")?.reader(),
                    JsonObject::class.java
                )
                for (i in ingsJson["data"].asJsonArray.asList().indices) {
                    val ing = ingsJson["data"].asJsonArray[i].asJsonObject
                    for (key in ingDefFields.keySet()) {
                        if (!ing.has(key)) {
                            ing.add(key, ingDefFields[key])
                        }
                    }
                }

                FileUtils.writeFile("config/WynnLib/Ingredients.json", ingsJson)
            }
        } catch (e: Exception) {
            // Recover
            val gear = FileUtils.readFile("config/WynnLib/Equipments.json")
            val ings = FileUtils.readFile("config/WynnLib/Ingredients.json")
            val fallback_data = gson.fromJson("{\"version\": \"null\",\"data\": [],\"md5\": \"\"}", JsonObject::class.java)

            if (gear == null) {
                FileUtils.writeFile("config/WynnLib/Equipments.json", fallback_data)
            }
            if (ings == null) {
                FileUtils.writeFile("config/WynnLib/Ingredients.json", fallback_data)
            }

            // Log
            println("WynnLib failed to auto update: $e")
            return false
        }
        return true
    }
}