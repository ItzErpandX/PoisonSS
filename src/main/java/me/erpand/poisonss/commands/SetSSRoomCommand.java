package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSSRoomCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;

    public SetSSRoomCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.only-players", "<red>Only players can execute this command.")));
            return true;
        }

        if (!player.hasPermission("op")) {
            player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission for this command.")));
            return true;
        }

        plugin.getDataManager().setSSRoom(player.getLocation());
        player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.ssroom-set", "<green>Successfully set SS room to your current location")));
        return true;
    }
}