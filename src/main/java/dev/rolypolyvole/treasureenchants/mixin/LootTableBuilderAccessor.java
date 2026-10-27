package dev.rolypolyvole.treasureenchants.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {

    @Accessor
    ImmutableList.Builder<LootPool> getPools();

    @Mutable
    @Accessor
    void setPools(ImmutableList.Builder<LootPool> pools);

    @Accessor
    ContextKeySet getParamSet();
}
