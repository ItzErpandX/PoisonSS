package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import me.erpand.poisonss.managers.LogManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdmitCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;
    private final LogManager logManager;

    public AdmitCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
        this.logManager = plugin.getLogManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.only-players", "<red>Only players can execute this command.")));
            return true;
        }

        if (!dataManager.isInSS(player)) {
            player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.not-being-ssed", "<red>You are not being screened.")));
            return true;
        }

        dataManager.logScreenshareResult(dataManager.getScreeningStaff(player).get().getUniqueId(), player.getUniqueId(), "admit");
        dataManager.removeFromSS(player);
        logManager.logAdmit(player);

        String punishCommand = plugin.getConfig().getString("punishments.admit", "tempban %player% 7d Admit")
                .replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand);

        player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.admit-message", "<red>You have been banned for admitting to cheating.")));
        return true;
    }
}