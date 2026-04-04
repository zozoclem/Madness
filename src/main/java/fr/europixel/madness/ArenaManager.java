package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ArenaManager {

    private final MadnessPlugin plugin;
    private final Random random;

    public ArenaManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void sendToArena(Player player) {
        Location spawn = getRandomArenaSpawn();

        if (spawn == null) {
            player.sendMessage("§cAucun spawn d'arène n'est configuré.");
            return;
        }

        plugin.getPlayerModeManager().setMode(player, PlayerMode.ARENA);
        player.teleport(spawn);
        plugin.getRechargeManager().clear(player);
        plugin.getKitManager().giveKit(player);
    }

    public Location getRandomArenaSpawn() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("arena-spawns");
        if (section == null) {
            return null;
        }

        Set<String> keys = section.getKeys(false);
        if (keys == null || keys.isEmpty()) {
            return null;
        }

        List<String> ids = new ArrayList<String>(keys);
        String chosenId = ids.get(random.nextInt(ids.size()));

        String path = "arena-spawns." + chosenId;

        String worldName = plugin.getConfig().getString(path + ".world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        double x = plugin.getConfig().getDouble(path + ".x");
        double y = plugin.getConfig().getDouble(path + ".y");
        double z = plugin.getConfig().getDouble(path + ".z");
        float yaw = (float) plugin.getConfig().getDouble(path + ".yaw");
        float pitch = (float) plugin.getConfig().getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public int addArenaSpawn(Location loc) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("arena-spawns");
        int nextId = 1;

        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String path = "arena-spawns." + nextId;
        plugin.getConfig().set(path + ".world", loc.getWorld().getName());
        plugin.getConfig().set(path + ".x", loc.getX());
        plugin.getConfig().set(path + ".y", loc.getY());
        plugin.getConfig().set(path + ".z", loc.getZ());
        plugin.getConfig().set(path + ".yaw", loc.getYaw());
        plugin.getConfig().set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();

        return nextId;
    }
}