package com.entrocorp.linearlogic.oneinthechamber;

import org.bukkit.plugin.java.JavaPlugin;

public class OITC extends JavaPlugin {

    public static OITC instance;

    public void onEnable() {
        instance = this;
        logInfo("Loading the config...");
        saveDefaultConfig();
        logInfo("Successfully enabled. Game on!");
    }

    public void onDisable() {
        logInfo("Saving the config...");
        saveConfig();
        logInfo("Successfully disabled. Game over!");
        instance = null;
    }

    public void logInfo(String msg) {
        getLogger().info(msg);
    }

    public void logWarning(String msg) {
        getLogger().warning(msg);
    }

    public void logSevere(String msg) {
        getLogger().severe(msg);
    }
}
