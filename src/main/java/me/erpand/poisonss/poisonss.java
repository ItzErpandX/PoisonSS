package me.erpand.poisonss;

import me.erpand.poisonss.commands.*;
import me.erpand.poisonss.listeners.*;
import me.erpand.poisonss.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class poisonss extends JavaPlugin {
    private static poisonss instance;
    private DataManager dataManager;
    private LogManager logManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load configuration files
        saveDefaultConfig();
        dataManager = new DataManager(this);
        logManager = new LogManager(this);
        cooldownManager = new CooldownManager(this);

        // Register commands
        getCommand("ss").setExecutor(new SSCommand(this));
        getCommand("ssclear").setExecutor(new ClearSSCommand(this));
        getCommand("sscheck").setExecutor(new SSCheckCommand(this));
        getCommand("setssroom").setExecutor(new SetSSRoomCommand(this));
        getCommand("setreleasezone").setExecutor(new SetReleaseZoneCommand(this));
        getCommand("ssreload").setExecutor(new ReloadCommand(this));
        getCommand("admit").setExecutor(new AdmitCommand(this));
        getCommand("sshelp").setExecutor(new SSHelpCommand(this));
        getCommand("sscheating").setExecutor(new SSPunishCommand(this, "cheating"));
        getCommand("ssno-cooperating").setExecutor(new SSPunishCommand(this, "no-cooperating"));
        getCommand("ssadmit").setExecutor(new SSPunishCommand(this, "admit"));
        getCommand("ssstat").setExecutor(new SSStatCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        getLogger().info("ScreenSharePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        dataManager.saveData();
        logManager.saveLogs();
        getLogger().info("ScreenSharePlugin has been disabled!");
    }

    public static poisonss getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}