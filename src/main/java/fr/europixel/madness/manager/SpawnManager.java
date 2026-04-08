package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SpawnManager {

    private final MadnessPlugin plugin;

    public SpawnManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public Location getSpawnLocation() {
        String worldName = plugin.getConfig().getString("spawn.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}