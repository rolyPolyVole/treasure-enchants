package dev.rolypolyvole.treasureenchants.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.rolypolyvole.treasureenchants.config.ModConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {

    @WrapWithCondition(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private boolean hideTreasureBookWarning(Logger logger, String message, Object lootTable, Object count) {
        return !(lootTable instanceof Identifier id && ModConfig.INSTANCE.getLootMap().containsKey(ResourceKey.create(Registries.LOOT_TABLE, id)));
    }
}
