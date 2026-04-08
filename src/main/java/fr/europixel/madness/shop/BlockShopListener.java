package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class BlockShopListener implements Listener {

    private final MadnessPlugin plugin;

    public BlockShopListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        if (!BlockShopMenu.getTitle(plugin).equals(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) {
            return;
        }

        String optionId = plugin.getBlockShopManager().getOptionIdBySlot(rawSlot);
        if (optionId == null) {
            return;
        }

        plugin.getBlockShopManager().handleClick(player, optionId);
        BlockShopMenu.open(plugin, player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        if (!BlockShopMenu.getTitle(plugin).equals(event.getView().getTitle())) {
            return;
        }

        Player player = (Player) event.getPlayer();
        player.setItemOnCursor(new ItemStack(Material.AIR));

        if (plugin.getPlayerModeManager().isInLobby(player)) {
            plugin.getLobbyManager().giveLobbyItems(player);
            player.getInventory().setHeldItemSlot(Math.max(0, Math.min(8, plugin.getConfig().getInt("lobby.selected-slot", 4))));
            player.updateInventory();
        }
    }
}