package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class SSStatCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;

    public SSStatCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.ssstat-usage")));
            return true;
        }

        OfflinePlayer staff = Bukkit.getOfflinePlayer(args[0]);
        if (staff == null) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.player-not-found")));
            return true;
        }

        Map<String, Integer> stats = dataManager.getScreenshareStats(staff.getUniqueId());

        Component message = mm.deserialize(
                plugin.getConfig().getString("messages.ssstat-header",
                        "<gold>ScreenShare Stats for <white><staff>"),
                Placeholder.unparsed("staff", staff.getName())
        );

        sender.sendMessage(message);
        sender.sendMessage(Component.empty());

        sender.sendMessage(mm.deserialize("<gray>Total Screenshares: <white>" + stats.getOrDefault("total", 0)));
        sender.sendMessage(mm.deserialize("<gray>Clean: <green>" + stats.getOrDefault("clean", 0)));
        sender.sendMessage(mm.deserialize("<gray>Cheating: <red>" + stats.getOrDefault("cheating", 0)));
        sender.sendMessage(mm.deserialize("<gray>No Cooperation: <red>" + stats.getOrDefault("no-cooperating", 0)));
        sender.sendMessage(mm.deserialize("<gray>Admitted: <red>" + stats.getOrDefault("admit", 0)));

        return true;
    }
}