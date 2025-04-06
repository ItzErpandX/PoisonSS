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

import java.util.Optional;
import java.util.UUID;

public class SSCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final DataManager dataManager;
    private final LogManager logManager;

    public SSCommand(poisonss plugin) {
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
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.ss-usage", "<red>Usage: /ss <player>")));
            plugin.getCooldownManager().removeCooldown(staff);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.player-offline", "<red>This player is not online.")));
            plugin.getCooldownManager().removeCooldown(staff);
            return true;
        }

        if (dataManager.isInSS(target)) {
            Optional<UUID> staffUUID = dataManager.getScreeningStaff(target.getUniqueId());
            String staffName = staffUUID.map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .orElse("Unknown Staff");

            Component message = mm.deserialize(
                    plugin.getConfig().getString(
                            "messages.already-ssed",
                            "<red><target> is already being screened by <staff>"
                    ),
                    Placeholder.unparsed("target", target.getName()),
                    Placeholder.unparsed("staff", staffName)
            );
            staff.sendMessage(message);
            plugin.getCooldownManager().removeCooldown(staff);
            return true;
        }

        if (target.hasPermission("ss.exempt")) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.cannot-ss", "<red>You cannot screen this player.")));
            plugin.getCooldownManager().removeCooldown(staff);
            return true;
        }

        if (target.equals(staff)) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.cannot-ss-self", "<red>You cannot screen yourself.")));
            plugin.getCooldownManager().removeCooldown(staff);
            return true;
        }

        if (plugin.getCooldownManager().isOnCooldown(staff)) {
            String cooldownMessage = plugin.getConfig().getString("messages.cooldown",
                            "<red>You screened someone <elapsed> ago, you may screen someone again in <remaining>.")
                    .replace("<elapsed>", plugin.getCooldownManager().getElapsedTime(staff))
                    .replace("<remaining>", plugin.getCooldownManager().getRemainingTime(staff));
            staff.sendMessage(mm.deserialize(cooldownMessage));
            return true;
        }

        Location ssRoom = dataManager.getSSRoom();
        if (ssRoom == null) {
            staff.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-ssroom", "<red>The screenshare room has not been set.")));
            return true;
        }

        dataManager.addToSS(target, staff);
        plugin.getCooldownManager().setCooldown(staff);
        logManager.logSS(staff, target);

        target.teleport(ssRoom);
        staff.teleport(ssRoom);

        Component title = mm.deserialize(plugin.getConfig().getString("messages.ss-title", "<gradient:gold:yellow>SCREENSHARE!"));
        Component subtitle = mm.deserialize(plugin.getConfig().getString("messages.ss-subtitle", "<white>Obey staff commands or you will be banned."));
        target.showTitle(net.kyori.adventure.title.Title.title(title, subtitle,
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(10 * 50),
                        java.time.Duration.ofMillis(70 * 50),
                        java.time.Duration.ofMillis(20 * 50))));

        Component broadcastMsg = mm.deserialize(
                plugin.getConfig().getString("messages.ss-broadcast",
                        "<dark_gray>[<light_purple>ꜱᴄʀᴇᴇɴ <white>ꜱʜᴀʀᴇ<dark_gray>]<light_purple> <target> <white>ɪꜱ ʙᴇɪɴɢ ꜱᴄʀᴇᴇɴᴇᴅ ʙʏ <light_purple><staff>"),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("staff", staff.getName()));

        Bukkit.getServer().broadcast(broadcastMsg);
        return true;
    }
}