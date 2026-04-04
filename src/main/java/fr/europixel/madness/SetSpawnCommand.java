package fr.europixel.madness;

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
            sender.sendMessage("§cCommande joueur uniquement.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("madness.admin")) {
            player.sendMessage("§cTu n'as pas la permission.");
            return true;
        }

        Location loc = player.getLocation();

        plugin.getConfig().set("lobby.world", loc.getWorld().getName());
        plugin.getConfig().set("lobby.x", loc.getX());
        plugin.getConfig().set("lobby.y", loc.getY());
        plugin.getConfig().set("lobby.z", loc.getZ());
        plugin.getConfig().set("lobby.yaw", loc.getYaw());
        plugin.getConfig().set("lobby.pitch", loc.getPitch());
        plugin.saveConfig();

        player.sendMessage("§aSpawn du lobby défini.");
        return true;
    }
}