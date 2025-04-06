package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class SSPunishCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;
    private final String punishType;

    public SSPunishCommand(poisonss plugin, String punishType) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
        this.punishType = punishType;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player staff)) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.only-players", "<red>Only players can execute this command.")));
            return true;
        }

        if (!staff.hasPermission("commands.staff")) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission for this command.")));
            return true;
        }

        // Get the player being screened by this staff
        Optional<Player> targetOptional = dataManager.getPlayerBeingScreenedBy(staff);

        if (!targetOptional.isPresent()) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.not-screening", "<red>You are not currently screening any player.")));
            return true;
        }

        Player target = targetOptional.get();
        String punishCommand = plugin.getConfig().getString("punishments." + punishType, "ban %player% 30d")
                .replace("%player%", target.getName());

        // Execute punishment
        dataManager.removeFromSS(target);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand);

        // Log the result
        dataManager.logScreenshareResult(staff.getUniqueId(), target.getUniqueId(), punishType);

        // Remove from screenshare
        dataManager.endScreenshare(target);

        // Send confirmation
        staff.sendMessage(mm.deserialize(
                plugin.getConfig().getString("messages.punish-success",
                        "<green>Banned <white><target> <green>for <white><reason>"),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("reason", punishType)
        ));

        return true;
    }
}