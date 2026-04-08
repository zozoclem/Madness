package fr.europixel.madness.listener.lobby;

import fr.europixel.madness.shop.BlockShopMenu;
import fr.europixel.madness.kit.EditKitMenu;
import fr.europixel.madness.item.ItemFactory;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyInteractListener implements Listener {

    private final MadnessPlugin plugin;

    public LobbyInteractListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (!plugin.getPlayerModeManager().isInLobby(player)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Action action = event.getAction();

        if (ItemFactory.isSimilarKeyItem(item, "play")) {
            event.setCancelled(true);

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                plugin.getArenaManager().sendToArena(player);
            }
            return;
        }

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ItemFactory.isSimilarKeyItem(item, "edit-kit")) {
            event.setCancelled(true);
            EditKitMenu.open(plugin, player);
            return;
        }

        if (ItemFactory.isSimilarKeyItem(item, "shop")) {
            event.setCancelled(true);
            BlockShopMenu.open(plugin, player);
        }
    }
}