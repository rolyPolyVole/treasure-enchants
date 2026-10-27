package dev.rolypolyvole.treasureenchants.config

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.storage.loot.LootTable

data class TreasureEnchantsConfig(
    val anvilCostLimit: Int = 60,
    val enchants: List<EnchantmentLootData> = listOf()
)

data class EnchantmentLootData(
    private val lootKey: String = "",
    val chance: Double = 5.0,
    val luckFactor: Double = 0.1,
    val enchantments: List<EnchantmentData> = listOf()
) {
    fun lootResourceKey(): ResourceKey<LootTable>? {
        val id = Identifier.tryParse(lootKey) ?: return null
        return ResourceKey.create(Registries.LOOT_TABLE, id)
    }
}

data class EnchantmentData(private val enchantKey: String = "", val level: Int = 1) {
    fun enchantResourceKey(): ResourceKey<Enchantment>? {
        val id = Identifier.tryParse(enchantKey) ?: return null
        return ResourceKey.create(Registries.ENCHANTMENT, id)
    }
}