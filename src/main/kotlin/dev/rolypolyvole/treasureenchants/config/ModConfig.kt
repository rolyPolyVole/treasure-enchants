package dev.rolypolyvole.treasureenchants.config

import com.google.gson.FormattingStyle
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import dev.rolypolyvole.treasureenchants.TreasureEnchantsMod
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.loot.LootTable
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText

@Suppress("UselessCallOnNotNull")
object ModConfig {
    private val gson = GsonBuilder()
        .setFormattingStyle(FormattingStyle.PRETTY.withIndent(" ".repeat(4)))
        .serializeNulls()
        .create()

    private val path = FabricLoader.getInstance()
        .configDir
        .resolve("${TreasureEnchantsMod.MOD_ID}.json")

    private val resourcePath = FabricLoader.getInstance()
        .getModContainer(TreasureEnchantsMod.MOD_ID)
        .flatMap { it.findPath("default_config.json") }
        .orElseThrow()

    var config = defaultConfig()
        private set
    var lootMap = buildLootMap()
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
            TreasureEnchantsMod.LOGGER.error("Failed to read config {}, falling back to default config", path, e)
            return
        }

        lootMap = buildLootMap()
    }

    private fun defaultConfig(): TreasureEnchantsConfig {
        return gson.fromJson(resourcePath.readText(), TreasureEnchantsConfig::class.java)
    }

    private fun buildLootMap(): Map<ResourceKey<LootTable>, List<EnchantmentLootData>> {
        return config.enchants.orEmpty().asSequence()
            .filter { validateNumbers(it).isEmpty() }
            .mapNotNull { data -> data.lootResourceKey()?.let { it to data } }
            .groupBy({ it.first }, { it.second })
    }

    fun validateConfig(server: MinecraftServer) {
        val lootTables = server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE)
        val enchantRegistry = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)

        for ((index, data) in config.enchants.orEmpty().withIndex()) {
            val problems = mutableListOf<String>()

            with(data) {
                problems += validateNumbers(data)

                val lootKey = lootResourceKey()

                if (lootKey == null || lootTables.get(lootKey).isEmpty) {
                    problems += "Invalid loot table resource location detected"
                }

                data.enchantments.orEmpty().forEachIndexed { i, enchant ->
                    val enchantKey = enchant.enchantResourceKey()

                    if (enchantKey == null || enchantRegistry.get(enchantKey).isEmpty) {
                        problems += "Enchant #${i + 1}: Invalid enchantment resource location detected"
                    }
                }
            }

            if (problems.isNotEmpty()) {
                TreasureEnchantsMod.LOGGER.error("Invalid configuration detected at index $index:\n${problems.joinToString("\n")}\n")
            }
        }
    }

    private fun validateNumbers(data: EnchantmentLootData): List<String> {
        val problems = mutableListOf<String>()

        if (data.chance <= 0) problems += "Chance has to be bigger than 0%"
        if (data.enchantments.isNullOrEmpty()) problems += "No configured enchantments found"

        data.enchantments.orEmpty().forEachIndexed { i, enchant ->
            if (enchant.level < 1) problems += "Enchant #${i + 1}: Level cannot be less than 1"
        }

        return problems
    }
}