package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class SSCheckCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;

    public SSCheckCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("commands.staff")) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission for this command.")));
            return true;
        }

        Set<UUID> ssPlayers = dataManager.getSSPlayers();
        if (ssPlayers.isEmpty()) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-players-in-ss", "<gray>No players are currently being ss-ed.")));
            return true;
        }

        Component message = mm.deserialize(plugin.getConfig().getString("messages.players-in-ss", "<gold>Players currently being ss-ed:"));
        sender.sendMessage(message);

        for (UUID uuid : ssPlayers) {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName != null) {
                sender.sendMessage(mm.deserialize("<gray>- <white>" + playerName));
            }
        }

        return true;
    }
}
