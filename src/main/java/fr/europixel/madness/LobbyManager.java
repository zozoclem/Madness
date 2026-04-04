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

        // hache sélectionnée directement
        player.getInventory().setHeldItemSlot(4);

        if (plugin.getSidebarManager() != null) {
            plugin.getSidebarManager().update(player);
        }
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, ItemFactory.createEditKitItem());
        player.getInventory().setItem(4, ItemFactory.createPlayAxe());

        player.updateInventory();
    }

    public Location getLobbySpawn() {
        String worldName = plugin.getConfig().getString("lobby.world");
        if (worldName == null || plugin.getServer().getWorld(worldName) == null) {
            return plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        return new Location(
                plugin.getServer().getWorld(worldName),
                plugin.getConfig().getDouble("lobby.x"),
                plugin.getConfig().getDouble("lobby.y"),
                plugin.getConfig().getDouble("lobby.z"),
                (float) plugin.getConfig().getDouble("lobby.yaw"),
                (float) plugin.getConfig().getDouble("lobby.pitch")
        );
    }
}