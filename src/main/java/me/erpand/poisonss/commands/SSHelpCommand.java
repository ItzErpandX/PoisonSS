package me.erpand.poisonss.commands;

import me.erpand.poisonss.poisonss;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SSHelpCommand implements CommandExecutor {
    private final poisonss plugin;
    private final MiniMessage mm;
    private final List<Component> defaultHelp;

    public SSHelpCommand(poisonss plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.defaultHelp = createDefaultHelp();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Component header = mm.deserialize(plugin.getConfig().getString("help.header", "<gradient:gold:yellow>ScreenShare Help</gradient>"));
        Component footer = mm.deserialize(
                plugin.getConfig().getString("help.footer", "<gray>Plugin version: <white><version>"),
                Placeholder.unparsed("version", plugin.getDescription().getVersion())
        );

        sender.sendMessage(Component.empty());
        sender.sendMessage(header);
        sender.sendMessage(Component.empty());

        // Get help messages from config or use defaults
        List<Component> helpMessages = getHelpMessages(sender);

        // Send all help messages
        helpMessages.forEach(sender::sendMessage);

        sender.sendMessage(Component.empty());
        sender.sendMessage(footer);
        return true;
    }

    private List<Component> getHelpMessages(CommandSender sender) {
        List<Component> messages = new ArrayList<>();

        // Try to get from config first
        if (plugin.getConfig().contains("help.commands")) {
            for (String cmd : plugin.getConfig().getConfigurationSection("help.commands").getKeys(false)) {
                if (sender.hasPermission(getPermissionForCommand(cmd))) {
                    String message = plugin.getConfig().getString("help.commands." + cmd);
                    if (message != null) {
                        messages.add(mm.deserialize(message));
                    }
                }
            }
            return messages;
        }

        // Fallback to default help if config section is missing
        return new ArrayList<>(defaultHelp);
    }

    private List<Component> createDefaultHelp() {
        List<Component> defaultHelp = new ArrayList<>();

        // Core Commands
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ss '><hover:show_text:'<gray>Initiate screenshare\n<white>Permission: commands.staff'><white>/ss <player></hover></click> - <gray>Start screensharing a player"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ssclear '><hover:show_text:'<gray>Clear from screenshare\n<white>Permission: commands.staff'><white>/ssclear <player></hover></click> - <gray>Release a player"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/sscheck'><hover:show_text:'<gray>Check active screenshares\n<white>Permission: commands.staff'><white>/sscheck</hover></click> - <gray>List screened players"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ssstat '><hover:show_text:'<gray>Check ss stats\n<white>Permission: commands.staff'><white>/ssstat <player></hover></click> - <gray>Check ss stats"));

        // Punishment Commands
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ssadmit'><hover:show_text:'<gray>Mark player as admitted guilt\n<white>Permission: commands.staff'><white>/ssadmit</hover></click> - <gray>Confirm player admission"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/sscheating'><hover:show_text:'<gray>Mark player as cheating\n<white>Permission: commands.staff'><white>/sscheating</hover></click> - <gray>Confirm cheating during SS"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ssno-cooperating'><hover:show_text:'<gray>Mark player as non-cooperative\n<white>Permission: commands.staff'><white>/ssno-cooperating</hover></click> - <gray>Confirm non-cooperation"));

        // Setup Commands
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/setssroom'><hover:show_text:'<gray>Set screenshare room location\n<white>Permission: op'><white>/setssroom</hover></click> - <gray>Set SS location"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/setreleasezone'><hover:show_text:'<gray>Set release zone location\n<white>Permission: op'><white>/setreleasezone</hover></click> - <gray>Set release point"));

        // Utility Commands
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/ssreload'><hover:show_text:'<gray>Reload plugin configuration\n<white>Permission: op'><white>/ssreload</hover></click> - <gray>Reload plugin files"));
        defaultHelp.add(mm.deserialize("<click:suggest_command:'/sshelp'><hover:show_text:'<gray>Show this help menu'><white>/sshelp</hover></click> - <gray>Command help"));

        return defaultHelp;
    }

    private String getPermissionForCommand(String command) {
        return switch (command.toLowerCase()) {
            case "ss", "ssclear", "sscheck" -> "commands.staff";
            case "setssroom", "setreleasezone", "ssreload" -> "op";
            case "admit" -> ""; // No permission needed
            default -> "screenshare.help";
        };
    }
}