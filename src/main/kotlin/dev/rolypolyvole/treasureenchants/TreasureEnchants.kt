package dev.rolypolyvole.treasureenchants

import dev.rolypolyvole.treasureenchants.config.ModConfig
import dev.rolypolyvole.treasureenchants.loot.LootGenerateEvent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TreasureEnchants : ModInitializer {

    override fun onInitialize() {
        ModConfig.load()
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register { _, _ -> ModConfig.load() }

        ServerLifecycleEvents.SERVER_STARTED.register { ModConfig.validateConfig(it) }
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { server, _, success -> if (success) ModConfig.validateConfig(server) }

        LootGenerateEvent.register()

        LOGGER.info("Initialized Treasure Enchants! Good luck in your adventures!")
    }

    companion object {
        const val MOD_ID = "treasure_enchants"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    }
}
