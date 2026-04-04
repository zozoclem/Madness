package fr.europixel.madness;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetArenaSpawnCommand implements CommandExecutor {

    private final MadnessPlugin plugin;

    public SetArenaSpawnCommand(MadnessPlugin plugin) {
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
        int id = plugin.getArenaManager().addArenaSpawn(loc);

        player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.arena-set", "&aSpawn d'arène ajouté avec l'id &f%id%&a.").replace("%id%", String.valueOf(id))));
        return true;
    }
}
