package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class LobbyManager {

    private final MadnessPlugin plugin;

    public LobbyManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public Location getLobbySpawn() {
        String worldName = plugin.getConfig().getString("lobby.world", "world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        double x = plugin.getConfig().getDouble("lobby.x", 0.5D);
        double y = plugin.getConfig().getDouble("lobby.y", 100.0D);
        double z = plugin.getConfig().getDouble("lobby.z", 0.5D);
        float yaw = (float) plugin.getConfig().getDouble("lobby.yaw", 0.0D);
        float pitch = (float) plugin.getConfig().getDouble("lobby.pitch", 0.0D);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void sendToLobby(Player player) {
        player.teleport(getLobbySpawn());
        plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
        giveLobbyItems(player);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setLevel(0);
        player.setExp(0.0F);
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta meta = axe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a§lJouer");
            meta.setLore(Collections.singletonList("§7Clique pour rejoindre l'arène"));
            axe.setItemMeta(meta);
        }

        player.getInventory().setItem(4, axe);
        player.updateInventory();
    }
}