package me.erpand.poisonss.managers;

import me.erpand.poisonss.poisonss;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private final poisonss plugin;
    private FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<UUID, UUID> activeSessions; // Player UUID -> Staff UUID
    private final Set<UUID> ssPlayers; // Players currently in SS
    private final Map<UUID, Location> preSSLocations; // Player UUID -> Location

    public DataManager(poisonss plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.activeSessions = new HashMap<>();
        this.ssPlayers = new HashSet<>();
        this.preSSLocations = new HashMap<>();
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadActiveSessions();
        loadSSPlayers();
    }

    private void loadActiveSessions() {
        activeSessions.clear();
        if (dataConfig.contains("active_sessions")) {
            for (String playerUUID : dataConfig.getConfigurationSection("active_sessions").getKeys(false)) {
                try {
                    UUID player = UUID.fromString(playerUUID);
                    UUID staff = UUID.fromString(dataConfig.getString("active_sessions." + playerUUID));
                    activeSessions.put(player, staff);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in active_sessions: " + playerUUID);
                }
            }
        }
    }

    private void loadSSPlayers() {
        ssPlayers.clear();
        for (String uuidStr : dataConfig.getStringList("ss_players")) {
            try {
                ssPlayers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in ss_players: " + uuidStr);
            }
        }
    }

    public void saveData() {
        try {
            // Save active sessions
            dataConfig.set("active_sessions", null);
            activeSessions.forEach((player, staff) ->
                    dataConfig.set("active_sessions." + player.toString(), staff.toString()));

            // Save SS players
            dataConfig.set("ss_players", ssPlayers.stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList()));

            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    // Original methods
    public boolean isInSS(Player player) {
        return ssPlayers.contains(player.getUniqueId());
    }

    public Set<UUID> getSSPlayers() {
        return new HashSet<>(ssPlayers);
    }

    public void addToSS(Player player, Player staff) {
        ssPlayers.add(player.getUniqueId());
        activeSessions.put(player.getUniqueId(), staff.getUniqueId());
        preSSLocations.put(player.getUniqueId(), player.getLocation());
        saveData();
    }

    // Add these methods to your existing DataManager class
    public Optional<UUID> getScreeningStaff(UUID playerUUID) {
        return Optional.ofNullable(activeSessions.get(playerUUID));
    }

    public void endScreenshare(Player player) {
        ssPlayers.remove(player.getUniqueId());
        activeSessions.remove(player.getUniqueId());
        preSSLocations.remove(player.getUniqueId());
        saveData();
    }

    // Keep this existing method as well

    public void removeFromSS(Player player) {
        ssPlayers.remove(player.getUniqueId());
        activeSessions.remove(player.getUniqueId());
        preSSLocations.remove(player.getUniqueId());
        saveData();
    }

    // New methods
    public Optional<Player> getScreeningStaff(Player target) {
        return Optional.ofNullable(activeSessions.get(target.getUniqueId()))
                .map(uuid -> plugin.getServer().getPlayer(uuid));
    }

    public Optional<Player> getPlayerBeingScreenedBy(Player staff) {
        return activeSessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(staff.getUniqueId()))
                .findFirst()
                .map(entry -> plugin.getServer().getPlayer(entry.getKey()));
    }

    public Optional<Location> getPreSSLocation(Player player) {
        return Optional.ofNullable(preSSLocations.get(player.getUniqueId()));
    }

    // Statistics tracking
    public void logScreenshareResult(UUID staffUUID, UUID playerUUID, String result) {
        if (!dataConfig.contains("stats." + staffUUID)) {
            dataConfig.set("stats." + staffUUID + ".total", 0);
            dataConfig.set("stats." + staffUUID + ".clean", 0);
            dataConfig.set("stats." + staffUUID + ".cheating", 0);
            dataConfig.set("stats." + staffUUID + ".no-cooperating", 0);
            dataConfig.set("stats." + staffUUID + ".admit", 0);
        }

        int total = dataConfig.getInt("stats." + staffUUID + ".total");
        dataConfig.set("stats." + staffUUID + ".total", total + 1);

        int current = dataConfig.getInt("stats." + staffUUID + "." + result);
        dataConfig.set("stats." + staffUUID + "." + result, current + 1);

        saveData();
    }

    public Map<String, Integer> getScreenshareStats(UUID staffUUID) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", dataConfig.getInt("stats." + staffUUID + ".total", 0));
        stats.put("clean", dataConfig.getInt("stats." + staffUUID + ".clean", 0));
        stats.put("cheating", dataConfig.getInt("stats." + staffUUID + ".cheating", 0));
        stats.put("no-cooperating", dataConfig.getInt("stats." + staffUUID + ".no-cooperating", 0));
        stats.put("admit", dataConfig.getInt("stats." + staffUUID + ".admit", 0));
        return stats;
    }

    // Location management
    public Location getSSRoom() {
        return dataConfig.getLocation("ss_room");
    }

    public void setSSRoom(Location location) {
        dataConfig.set("ss_room", location);
        saveData();
    }

    public Location getReleaseZone() {
        return dataConfig.getLocation("release_zone");
    }

    public void setReleaseZone(Location location) {
        dataConfig.set("release_zone", location);
        saveData();
    }

    public void reloadData() {
        loadData();
    }
}