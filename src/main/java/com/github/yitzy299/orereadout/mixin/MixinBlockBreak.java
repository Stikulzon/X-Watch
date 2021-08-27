package com.github.yitzy299.orereadout.mixin;

import com.github.yitzy299.orereadout.Action;
import com.github.yitzy299.orereadout.OreReadout;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Block.class)
public class MixinBlockBreak {
    @Inject(method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        Block block = state.getBlock();
        OreReadout.CONFIG.actions.stream().filter(a -> a.getBlock().equals(block)).forEach(action -> display(action, pos, player));
    }
    private void display(Action action, BlockPos pos, PlayerEntity player) {
        var log = player.getName().asString() + " broke " + action.getBlock().getName().getString() + " at " + pos.getX() + " " + pos.getY()+ " " + pos.getZ();
        var text = player.getName().shallowCopy();
        text.append(" broke ");
        text.append(action.getBlock().getName());
        text.append(" at [ ");
        var posMsg = new LiteralText(pos.getX() + " " + pos.getY() + " " + pos.getZ()).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in " + player.getEntityWorld().getRegistryKey().getValue() + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ())));
        text.append(posMsg);
        text.append(" ]");
        text.setStyle(Style.EMPTY.withColor(Formatting.YELLOW));

        if (action.isConsole()) {
            OreReadout.LOG.info(log);
        }
        if (action.isOpChat()) {
            Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList().stream().filter(p -> p.hasPermissionLevel(3)).forEach(p -> p.sendMessage(text, false));
        }
        if (action.isPublicChat()) {
            Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList().forEach(p -> p.sendMessage(text, false));
        }
    }

}
