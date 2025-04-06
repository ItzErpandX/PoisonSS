package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;

    public ReloadCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("screenshare.reload")) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission for this command.")));
            return true;
        }

        plugin.reloadConfig();
        plugin.getDataManager().reloadData();
        sender.sendMessage(mm.deserialize("<green>ScreenShare configuration and data reloaded successfully!"));
        return true;
    }
}