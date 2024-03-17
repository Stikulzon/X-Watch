package com.github.stikulzon.xwatch;

import com.github.stikulzon.xwatch.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class XrayCatcher {
    public static void trigger (String action, BlockPos pos, PlayerEntity player) {
        sendMessage(action, pos, player);
    }
    public static void sendMessage(String action, BlockPos pos, PlayerEntity player) {
        var log = player.getName().getString() + " broke " + action + " at " + pos.getX() + " " + pos.getY()+ " " + pos.getZ();
        var text = player.getName().copy();
        text.append(" broke ");
        text.append(action);
        text.append(" at [ ");

        var world = player.getWorld();
        var registryKey = world.getRegistryKey().getValue();
        var posMsg  = Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ())
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in " + Identifier.tryParse(registryKey.toString()) + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ())));
        text.append(posMsg);
        text.append(" ]");
        text.setStyle(Style.EMPTY.withColor(Formatting.YELLOW));

        if (ConfigManager.isLogToConsoleEnabled()) {
            XWatch.LOGGER.info(log);
        }
        if (ConfigManager.isLogToOpChatEnabled() || Permissions.check(player, "xwatch.subscribe")) {
            Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList().stream().filter(p -> p.hasPermissionLevel(3)).forEach(p -> p.sendMessage(text, false));
        }
        if (ConfigManager.isLogToPublicChat()) {
            Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList().forEach(p -> p.sendMessage(text, false));
        }
    }
}
