package com.github.stikulzon.xwatch;

import com.github.stikulzon.xwatch.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XWatch implements ModInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);

        ConfigManager.configsInit();
    }

    private void onServerStarting(MinecraftServer server) {
        SERVER = server;
        LOGGER.info("initialized");
    }
}
