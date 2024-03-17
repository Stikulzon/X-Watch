package com.github.stikulzon.xwatch.mixin;

import com.github.stikulzon.xwatch.XrayCatcher;
import com.github.stikulzon.xwatch.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockBreakMixin {
    @Inject(method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        ConfigManager.getBlockList().stream().filter(a -> Registries.BLOCK.get(new Identifier(a)).equals(state.getBlock())).forEach(block -> XrayCatcher.trigger(block, pos, player));
    }
}
