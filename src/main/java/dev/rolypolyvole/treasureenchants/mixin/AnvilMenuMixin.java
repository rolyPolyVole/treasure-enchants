package dev.rolypolyvole.treasureenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.rolypolyvole.treasureenchants.config.ModConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int keepTreasureLevels(int maxLevel, @Local(name = "current") int current, @Local(name = "entry") Object2IntMap.Entry<Holder<Enchantment>> entry) {
        return Math.max(maxLevel, Math.max(current, entry.getIntValue()));
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;areCompatible(Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)Z"))
    private boolean allowTreasureEnchantmentConflicts(Holder<Enchantment> enchantment, Holder<Enchantment> other, Operation<Boolean> original, @Local(name = "current") int current, @Local(name = "additionalEnchantments") ItemEnchantments additionalEnchantments) {
        return original.call(enchantment, other) || current > 0 || additionalEnchantments.getLevel(other) > 0;
    }

    @ModifyExpressionValue(method = "createResult", slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=39")), at = @At(value = "CONSTANT", args = "intValue=40", ordinal = 0))
    private int increaseMaxCost(int limit, @Local(name = "input") ItemStack input) {
        return input.getCount() > 1 ? limit : ModConfig.INSTANCE.getConfig().getAnvilCostLimit();
    }
}
