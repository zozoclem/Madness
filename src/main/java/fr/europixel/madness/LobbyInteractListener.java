package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyInteractListener implements Listener {

    private final MadnessPlugin plugin;

    public LobbyInteractListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getPlayerModeManager().isInLobby(player)) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (isPlayItem(item)) {
            event.setCancelled(true);
            plugin.getArenaManager().sendToArena(player);
            return;
        }

        if (isEditKitItem(item)) {
            event.setCancelled(true);
            EditKitMenu.open(plugin, player);
            return;
        }
    }

    private boolean isPlayItem(ItemStack item) {
        return item.getType() == Material.DIAMOND_AXE && hasName(item, "§a§lJouer");
    }

    private boolean isEditKitItem(ItemStack item) {
        return item.getType() == Material.BLAZE_ROD && hasName(item, "§e§lEditKit");
    }

    private boolean hasName(ItemStack item, String expected) {
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && expected.equals(meta.getDisplayName());
    }
}