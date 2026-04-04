package fr.europixel.madness;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LobbyManager {

    private final MadnessPlugin plugin;

    public LobbyManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendToLobby(Player player) {
        player.teleport(getLobbySpawn());
        plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);

        giveLobbyItems(player);

        player.getInventory().setHeldItemSlot(Math.max(0, Math.min(8, plugin.getConfig().getInt("lobby.selected-slot", 4))));

        if (plugin.getSidebarManager() != null) {
            plugin.getSidebarManager().update(player);
        }
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(plugin.getConfig().getInt("lobby.items.edit-kit.slot", 0), ItemFactory.createEditKitItem());
        player.getInventory().setItem(plugin.getConfig().getInt("lobby.items.play.slot", 4), ItemFactory.createPlayAxe());

        player.updateInventory();
    }

    public Location getLobbySpawn() {
        String worldName = plugin.getConfig().getString("lobby.spawn.world");
        if (worldName == null || plugin.getServer().getWorld(worldName) == null) {
            return plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        return new Location(
                plugin.getServer().getWorld(worldName),
                plugin.getConfig().getDouble("lobby.spawn.x"),
                plugin.getConfig().getDouble("lobby.spawn.y"),
                plugin.getConfig().getDouble("lobby.spawn.z"),
                (float) plugin.getConfig().getDouble("lobby.spawn.yaw"),
                (float) plugin.getConfig().getDouble("lobby.spawn.pitch")
        );
    }
}
