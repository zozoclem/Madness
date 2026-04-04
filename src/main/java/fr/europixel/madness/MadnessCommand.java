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
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /madness reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("madness.reload")) {
                sender.sendMessage("§cTu n'as pas la permission.");
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage("§aMadness a ete recharge.");
            return true;
        }

        sender.sendMessage("§cUsage: /madness reload");
        return true;
    }
}