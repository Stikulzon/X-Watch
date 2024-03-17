package com.github.stikulzon.xwatch.config;

import com.github.stikulzon.xwatch.XWatch;
import net.fabricmc.loader.api.FabricLoader;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("XWatch");
    private static List<String> blocks;
    private static Boolean isLogToOpChatEnabled;
    private static Boolean isLogToConsoleEnabled;
    private static Boolean isLogToPublicChat;

    public static void configsInit() {
        loadConfig();
    }
    public static void loadConfig() {

        Path configPath = ConfigManager.CONFIG_DIR.resolve("config.yml");
        YamlFile yamlFile = new YamlFile(configPath.toAbsolutePath().toString());

        Path defaultConfigPath = FabricLoader.getInstance().getModContainer("xwatch").flatMap(servercosmetics -> servercosmetics.findPath("assets/xwatch/configs/")).get().resolve("default_config.yml");
        try {
            Path tempFile = Files.createTempFile("tempFile_", ".yml");
            Files.copy(defaultConfigPath, tempFile, StandardCopyOption.REPLACE_EXISTING);
            YamlFile defaultYamlFile = new YamlFile(tempFile.toAbsolutePath().toString());

            defaultYamlFile.createOrLoadWithComments();
            yamlFile.createOrLoadWithComments();
            setupDefaultConfig(yamlFile, configPath);
            yamlFile.loadWithComments();

            isLogToOpChatEnabled = (Boolean) getRequiredConfigValue("op_chat", yamlFile, defaultYamlFile);
            isLogToConsoleEnabled = (Boolean) getRequiredConfigValue("log", yamlFile, defaultYamlFile);
            isLogToPublicChat = (Boolean) getRequiredConfigValue("public_chat", yamlFile, defaultYamlFile);

            var tempBlocksVar = getRequiredConfigValue("blocks", yamlFile, yamlFile);
            if(tempBlocksVar instanceof String) {
                blocks = new ArrayList<>();
                blocks.add((String) tempBlocksVar);
            } else {
                System.out.println("blockList: "+ getRequiredConfigValue("blocks", yamlFile, defaultYamlFile));
                blocks = (List<String>) getRequiredConfigValue("blocks", yamlFile, defaultYamlFile);
                System.out.println("blockList.size: "+ blocks.size());
            }

            Files.delete(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create or load cosmeticsGUI.yml file", e);
        }
    }

    public static List<String> getBlockList(){
        return blocks;
    }
    public static boolean isLogToOpChatEnabled(){
        return isLogToOpChatEnabled;
    }
    public static boolean isLogToConsoleEnabled(){
        return isLogToConsoleEnabled;
    }
    public static boolean isLogToPublicChat(){
        return isLogToPublicChat;
    }

    public static Object getRequiredConfigValue(String key, YamlFile yamlFile, YamlFile defaultConfigFile){
        var result = yamlFile.get(key);
        if (result != null){
            return result;
        } else {
            var defaultValue = defaultConfigFile.get(key);
            yamlFile.addDefault(key, defaultConfigFile.get(key));
            try {
                yamlFile.save();
                yamlFile.loadWithComments();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load or save configuration file", e);
            }
            return defaultValue;
        }
    }

    private static void setupDefaultConfig(YamlFile yamlFile, Path configPath){
        if (!configPath.toFile().exists()) {
            try {
                Files.copy(Objects.requireNonNull(XWatch.class.getResourceAsStream("/assets/xwatch/configs/default_config.yml")), configPath);
                XWatch.LOGGER.info("[XCatch] Config file created in config/config.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            yamlFile.save();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save default yml configuration", e);
        }
    }

//    public static void configInit(){
//        var configPath = FabricLoader.getInstance().getConfigDir().resolve("config.json");
//        if (!configPath.toFile().exists()) {
//            try {
//                Files.copy(Objects.requireNonNull(XWatch.class.getResourceAsStream("/assets/xwatch/configs/default_config.yml")), configPath);
//                XWatch.LOGGER.info("[XCatch] Config file created in config/config.json");
//                readProperties(configPath);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        readProperties(configPath);
//    }

//    private static void readProperties(Path path) {
//        List<Action> actions = new ArrayList<>();
//        try {
//            var array = JsonParser.parseString(Files.readString(path)).getAsJsonArray();
//            array.forEach(e -> {
//                var o = e.getAsJsonObject();
//                var block = new Identifier(o.get("block").getAsString());
//                var opChat = o.get("op_chat").getAsBoolean();
//                var publicChat = o.get("public_chat").getAsBoolean();
//                var console = o.get("log").getAsBoolean();
//                var action = new Action(block, opChat, publicChat, console);
//                actions.add(action);
//            });
//            CONFIG = new ConfigManager(actions);
//        } catch (IOException e) {
//            CONFIG = new ConfigManager(actions);
//        }
//    }
}
