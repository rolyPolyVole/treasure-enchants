package dev.rolypolyvole.treasureenchants.config

import com.google.gson.FormattingStyle
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import dev.rolypolyvole.treasureenchants.TreasureEnchants
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.loot.LootTable
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText

object ModConfig {
    private val gson = GsonBuilder()
        .setFormattingStyle(FormattingStyle.PRETTY.withIndent(" ".repeat(8)))
        .serializeNulls()
        .create()

    private val path = FabricLoader.getInstance()
        .configDir
        .resolve("${TreasureEnchants.MOD_ID}.json")

    private val resourcePath = FabricLoader.getInstance()
        .getModContainer(TreasureEnchants.MOD_ID)
        .flatMap { it.findPath("default_config.json") }
        .orElseThrow()

    var config = defaultConfig()
        private set
    var lootMap = getLootMap()
        private set

    fun load() {
        config = try {
            if (path.exists()) {
                gson.fromJson(path.readText(), TreasureEnchantsConfig::class.java) ?: defaultConfig()
            } else {
                Files.copy(resourcePath, path)
                defaultConfig()
            }
        } catch (e: JsonParseException) {
            TreasureEnchants.LOGGER.error("Failed to read config {}, falling back to default config", path, e)
            return
        }

        lootMap = getLootMap()
    }

    private fun defaultConfig(): TreasureEnchantsConfig {
        return gson.fromJson(resourcePath.readText(), TreasureEnchantsConfig::class.java)
    }

    private fun getLootMap(): Map<ResourceKey<LootTable>, List<EnchantmentLootData>> {
        return config.enchants.orEmpty().asSequence()
            .filter { it.chance >= 0 && it.level >= 1 }
            .mapNotNull { data -> data.lootResourceKey()?.let { it to data } }
            .groupBy({ it.first }, { it.second })
    }

    fun validateConfig(server: MinecraftServer) {
        val lootTables = server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE)
        val enchantments = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)

        for ((index, data) in config.enchants.withIndex()) {
            val problems = mutableListOf<String>()

            with(data) {
                if (chance <= 0) {
                    problems += "Chance has to be bigger than 0%"
                }

                if (level < 1) {
                    problems += "Enchantment level cannot be less than 1"
                }

                val lootKey = lootResourceKey()
                val enchantKey = enchantResourceKey()

                if (lootKey == null || lootTables.get(lootKey).isEmpty) {
                    problems += "Invalid loot table resource location detected"
                }

                if (enchantKey == null || enchantments.get(enchantKey).isEmpty) {
                    problems += "Invalid enchantment resource location detected"
                }
            }

            if (problems.isNotEmpty()) {
                TreasureEnchants.LOGGER.error("Invalid configuration detected at index $index:\n${problems.joinToString("\n")}\n")
            }
        }
    }
}