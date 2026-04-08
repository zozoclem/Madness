package fr.europixel.madness.manager;

import fr.europixel.madness.item.ItemFactory;
import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerMode;
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

        player.getInventory().setHeldItemSlot(
                Math.max(0, Math.min(8, plugin.getConfig().getInt("lobby.selected-slot", 4)))
        );

        if (plugin.getSidebarManager() != null) {
            plugin.getSidebarManager().update(player);
        }
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        int editKitSlot = plugin.getConfig().getInt("lobby.items.edit-kit.slot", 0);
        int playSlot = plugin.getConfig().getInt("lobby.items.play.slot", 4);
        int shopSlot = plugin.getConfig().getInt("lobby.items.shop.slot", 8);

        player.getInventory().setItem(editKitSlot, ItemFactory.createEditKitItem());
        player.getInventory().setItem(playSlot, ItemFactory.createPlayAxe());
        player.getInventory().setItem(shopSlot, ItemFactory.createShopItem());

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