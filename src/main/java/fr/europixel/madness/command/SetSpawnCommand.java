package fr.europixel.madness.command;

import fr.europixel.madness.utils.ConfigUtil;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final MadnessPlugin plugin;

    public SetSpawnCommand(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.player-only", "&cCommande joueur uniquement.")));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("madness.admin")) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.no-permission", "&cTu n'as pas la permission.")));
            return true;
        }

        Location loc = player.getLocation();

        plugin.getConfig().set("lobby.spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("lobby.spawn.x", loc.getX());
        plugin.getConfig().set("lobby.spawn.y", loc.getY());
        plugin.getConfig().set("lobby.spawn.z", loc.getZ());
        plugin.getConfig().set("lobby.spawn.yaw", loc.getYaw());
        plugin.getConfig().set("lobby.spawn.pitch", loc.getPitch());
        plugin.saveConfig();

        player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.lobby-set", "&aSpawn du lobby défini.")));
        return true;
    }
}
