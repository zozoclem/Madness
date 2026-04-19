package fr.europixel.madness.command;

import fr.europixel.madness.utils.ConfigUtil;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MadnessCommand implements CommandExecutor {

    private final MadnessPlugin plugin;

    public MadnessCommand(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ConfigUtil.color(plugin.getConfig().getString(
                "messages.commands.madness-usage",
                "&cUsage: /madness reload|setpos1|setpos2"
        ));

        if (args.length == 0) {
            sender.sendMessage(usage);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("madness.reload")) {
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.no-permission", "&cTu n'as pas la permission.")));
                return true;
            }

            plugin.reloadConfig();
            sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.reload", "&aMadness a été rechargé.")));
            return true;
        }

        if (args[0].equalsIgnoreCase("setpos1") || args[0].equalsIgnoreCase("setpos2")) {
            if (!sender.hasPermission("madness.admin")) {
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.no-permission", "&cTu n'as pas la permission.")));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.player-only", "&cPlayer-only command.")));
                return true;
            }

            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("setpos1")) {
                plugin.getArenaManager().setArenaPos1(player.getLocation());
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.arena-pos1-set", "&aArena pos1 has been set.")));
            } else {
                plugin.getArenaManager().setArenaPos2(player.getLocation());
                sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.arena-pos2-set", "&aArena pos2 has been set.")));
            }
            return true;
        }

        sender.sendMessage(usage);
        return true;
    }
}
