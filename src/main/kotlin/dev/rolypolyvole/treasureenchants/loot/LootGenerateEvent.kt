package dev.rolypolyvole.treasureenchants.loot

import com.google.common.collect.ImmutableList
import dev.rolypolyvole.treasureenchants.config.ModConfig
import dev.rolypolyvole.treasureenchants.mixin.LootTableBuilderAccessor
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Items
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders
import kotlin.math.roundToInt

object LootGenerateEvent {
    fun register() {
        LootTableEvents.MODIFY.register { key, builder, _, provider ->
            val entries = ModConfig.lootMap[key] ?: return@register
            val registry = provider.lookupOrThrow(Registries.ENCHANTMENT)

            entries.forEach {
                val enchantments = it.enchantments.map { data ->
                    val resourceKey = data.enchantResourceKey() ?: return@forEach
                    val enchant = registry.get(resourceKey).orElse(null) ?: return@forEach
                    enchant to data.level
                }.toMap()

                val weight = (it.chance.coerceAtMost(100.0) * 10).roundToInt()
                val quality = (it.luckFactor * 10).roundToInt()

                val pool = LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(weight)
                        .setQuality(quality)
                        .apply(SetEnchantmentsFunction.Builder().apply {
                            enchantments.forEach { (enchant, level) -> withEnchantment(enchant, ContextIntProviders.exactly(level)) }
                        })
                    )
                    .add(EmptyLootItem.emptyItem().setWeight(1000 - weight))

                val accessor = builder as LootTableBuilderAccessor

                // For archaeology loot (brushing gravel), move the pool into index 0, because it only takes the first item
                if (accessor.paramSet == LootContextParamSets.ARCHAEOLOGY) {
                    accessor.pools = ImmutableList.builder<LootPool>().add(pool.build()).addAll(accessor.pools.build())
                } else {
                    builder.withPool(pool)
                }
            }
        }
    }
}