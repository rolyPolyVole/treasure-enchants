package dev.rolypolyvole.treasureenchants.loot

import dev.rolypolyvole.treasureenchants.config.ModConfig
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Items
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders
import kotlin.math.roundToInt

object LootGenerateEvent {
    fun register() {
        LootTableEvents.MODIFY.register { key, builder, _, provider ->
            val entries = ModConfig.lootMap[key] ?: return@register
            val enchants = provider.lookupOrThrow(Registries.ENCHANTMENT)

            entries.forEach {
                val enchantKey = it.enchantResourceKey() ?: return@forEach
                val enchantment = enchants.get(enchantKey).orElse(null) ?: return@forEach

                val weight = (it.chance.coerceAtMost(100.0) * 10).roundToInt()
                val quality = (it.luckFactor * 10).roundToInt()

                builder.withPool(
                    LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .setWeight(weight)
                            .setQuality(quality)
                            .apply(SetEnchantmentsFunction.Builder()
                                .withEnchantment(enchantment, ContextIntProviders.exactly(it.level))
                            )
                        )
                        .add(EmptyLootItem.emptyItem().setWeight(1000 - weight))
                )
            }
        }
    }
}