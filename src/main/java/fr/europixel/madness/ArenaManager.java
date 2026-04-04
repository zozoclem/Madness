package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ArenaManager {

    private final MadnessPlugin plugin;
    private int nextSpawnIndex;

    public ArenaManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        this.nextSpawnIndex = 0;
    }

    public void sendToArena(Player player) {
        Location spawn = getNextArenaSpawn();

        if (spawn == null) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.commands.no-arena-spawn", "&cAucun spawn d\'arène n\'est configuré.")));
            return;
        }

        plugin.getPlayerModeManager().setMode(player, PlayerMode.ARENA);
        player.teleport(spawn);
        plugin.getRechargeManager().clear(player);
        plugin.getKitManager().giveKit(player);
        plugin.getSidebarManager().update(player);
    }

    public Location getNextArenaSpawn() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("arena-spawns");
        if (section == null) {
            return null;
        }

        Set<String> keys = section.getKeys(false);
        if (keys == null || keys.isEmpty()) {
            return null;
        }

        List<Integer> ids = new ArrayList<Integer>();

        for (String key : keys) {
            try {
                ids.add(Integer.parseInt(key));
            } catch (NumberFormatException ignored) {
            }
        }

        if (ids.isEmpty()) {
            return null;
        }

        Collections.sort(ids);

        if (nextSpawnIndex >= ids.size()) {
            nextSpawnIndex = 0;
        }

        int chosenId = ids.get(nextSpawnIndex);
        nextSpawnIndex++;

        String path = "arena-spawns." + chosenId;

        String worldName = plugin.getConfig().getString(path + ".world");
        if (worldName == null) {
            return null;
        }

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