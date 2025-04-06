package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import me.erpand.poisonss.managers.DataManager;
import me.erpand.poisonss.managers.LogManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearSSCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;
    private final LogManager logManager;

    public ClearSSCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.dataManager = plugin.getDataManager();
        this.logManager = plugin.getLogManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player staff)) {
            sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.only-players", "<red>Only players can execute this command.")));
            return true;
        }

        if (!staff.hasPermission("commands.staff")) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission for this command.")));
            return true;
        }

        if (args.length == 0) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.clearss-usage", "<red>Usage: /clearss <player>")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.player-offline", "<red>This player is not online.")));
            return true;
        }

        if (!dataManager.isInSS(target)) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.not-ssed", "<red>That player is not being screened.")));
            return true;
        }

        Location releaseZone = dataManager.getReleaseZone();
        if (releaseZone == null) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-releasezone", "<red>The release zone has not been set.")));
            return true;
        }

        dataManager.logScreenshareResult(dataManager.getScreeningStaff(target).get().getUniqueId(), target.getUniqueId(), "clean");
        dataManager.removeFromSS(target);
        logManager.logClear(staff, target);

        Component title = mm.deserialize(plugin.getConfig().getString("messages.cleared-title", "<green>CLEARED!"));
        Component subtitle = mm.deserialize(
                plugin.getConfig().getString("messages.cleared-subtitle", "<white>by <staff>"),
                Placeholder.unparsed("staff", staff.getName())
        );

        target.showTitle(net.kyori.adventure.title.Title.title(
                title,
                subtitle,
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(10 * 50),
                        java.time.Duration.ofMillis(70 * 50),
                        java.time.Duration.ofMillis(20 * 50)
                )
        ));

        target.teleport(releaseZone);

        Component broadcastMsg = mm.deserialize(
                plugin.getConfig().getString("messages.clear-broadcast",
                        "<dark_gray>[<light_purple>ꜱᴄʀᴇᴇɴ <white>ꜱʜᴀʀᴇ<dark_gray>]<light_purple> <target> <white>ɪꜱ ʙᴇɪɴɢ ʀᴇʟᴇᴀꜱᴇᴅ ʙʏ <light_purple><staff>"),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("staff", staff.getName())
        );

        Bukkit.getServer().broadcast(broadcastMsg);
        return true;
    }
}