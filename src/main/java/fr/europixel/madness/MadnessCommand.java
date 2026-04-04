package fr.europixel.madness;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MadnessCommand implements CommandExecutor {

    private final MadnessPlugin plugin;

    public MadnessCommand(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ConfigUtil.color(plugin.getConfig().getString("messages.commands.madness-usage", "&cUsage: /madness reload"));

        if (args.length == 0) {
            sender.sendMessage(usage);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("madness.reload")) {
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.no-permission", "&cTu n'as pas la permission.")));
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.reload", "&aMadness a été rechargé.")));
            return true;
        }

        sender.sendMessage(usage);
        return true;
    }
}
