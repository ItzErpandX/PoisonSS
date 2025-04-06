package me.erpand.poisonss.listeners;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;

    public CommandListener(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!dataManager.isInSS(player)) {
            return;
        }

        String command = event.getMessage().split(" ")[0].toLowerCase().substring(1); // Remove the leading slash

        // Check whitelist
        List<String> whitelist = plugin.getConfig().getStringList("command-restrictions.whitelist");
        if (whitelist.contains(command)) {
            return;
        }

        // Check blacklist
        List<String> blacklist = plugin.getConfig().getStringList("command-restrictions.blacklist");
        if (blacklist.contains(command)) {
            dataManager.logScreenshareResult(dataManager.getScreeningStaff(player).get().getUniqueId(), player.getUniqueId(), "no-cooperating");
            dataManager.removeFromSS(player);
            String punishCommand = plugin.getConfig().getString("punishments.blacklisted-command", "tempban %player% 7d Using restricted command while being checked.")
                    .replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand);
            event.setCancelled(true);
            return;
        }

        // Block all other commands
        event.setCancelled(true);
        player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-commands", "<red>You cannot use commands while being screened!")));
    }
}