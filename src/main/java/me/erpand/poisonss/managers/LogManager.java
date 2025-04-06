package me.erpand.poisonss.managers;

import me.erpand.poisonss.poisonss;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private final poisonss plugin;
    private FileConfiguration logConfig;
    private File logFile;

    public LogManager(poisonss plugin) {
        this.plugin = plugin;
        loadLogs();
    }

    private void loadLogs() {
        logFile = new File(plugin.getDataFolder(), "log.yml");
        if (!logFile.exists()) {
            plugin.saveResource("log.yml", false);
        }
        logConfig = YamlConfiguration.loadConfiguration(logFile);
    }

    public void saveLogs() {
        try {
            logConfig.save(logFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save log.yml: " + e.getMessage());
        }
    }

    public void logSS(Player staff, Player target) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] %s screened %s", timestamp, staff.getName(), target.getName());

        List<String> logs = logConfig.getStringList("logs");
        logs.add(entry);
        logConfig.set("logs", logs);
        saveLogs();
    }

    public void logClear(Player staff, Player target) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] %s cleared %s from screenshare", timestamp, staff.getName(), target.getName());

        List<String> logs = logConfig.getStringList("logs");
        logs.add(entry);
        logConfig.set("logs", logs);
        saveLogs();
    }

    public void logAdmit(Player player) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] %s admitted to cheating", timestamp, player.getName());

        List<String> logs = logConfig.getStringList("logs");
        logs.add(entry);
        logConfig.set("logs", logs);
        saveLogs();
    }

    public List<Component> getFormattedLogs() {
        List<Component> formattedLogs = new ArrayList<>();
        MiniMessage mm = MiniMessage.miniMessage();

        for (String log : logConfig.getStringList("logs")) {
            formattedLogs.add(mm.deserialize("<gray>" + log));
        }

        return formattedLogs;
    }
}