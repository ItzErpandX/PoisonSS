package me.erpand.poisonss.managers;

import me.erpand.poisonss.poisonss;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final poisonss plugin;
    private final Map<UUID, Long> cooldowns;
    private long cooldownTime;

    public CooldownManager(poisonss plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.cooldownTime = plugin.getConfig().getLong("cooldown-time", 120) * 1000;
    }

    public boolean isOnCooldown(Player player) {
        if (player.hasPermission("screenshare.bypass.cooldown")) {
            return false;
        }

        return cooldowns.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) < cooldownTime;
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public String getRemainingTime(Player player) {
        if (!isOnCooldown(player)) {
            return "0 seconds";
        }

        long remaining = (cooldownTime - (System.currentTimeMillis() - cooldowns.get(player.getUniqueId()))) / 1000;
        return remaining + " seconds";
    }

    public String getElapsedTime(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return "never";
        }

        long elapsed = (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000;
        return elapsed + " seconds ago";
    }
}