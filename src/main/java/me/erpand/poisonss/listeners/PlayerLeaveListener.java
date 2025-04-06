package me.erpand.poisonss.listeners;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    private final poisonss plugin;
    private final DataManager dataManager;

    public PlayerLeaveListener(poisonss plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (dataManager.isInSS(player)) {
            dataManager.logScreenshareResult(dataManager.getScreeningStaff(player).get().getUniqueId(), player.getUniqueId(), "no-cooperating");
            dataManager.removeFromSS(player);
            String punishCommand = plugin.getConfig().getString("punishments.leave", "tempban %player% 14d Leaving while being checked.")
                    .replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand);
        }
    }
}