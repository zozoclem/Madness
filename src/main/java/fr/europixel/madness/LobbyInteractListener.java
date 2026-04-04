package fr.europixel.madness;

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

        if (ItemFactory.isSimilarKeyItem(item, "play")) {
            event.setCancelled(true);
            plugin.getArenaManager().sendToArena(player);
            return;
        }

        if (ItemFactory.isSimilarKeyItem(item, "edit-kit")) {
            event.setCancelled(true);
            EditKitMenu.open(plugin, player);
        }
    }
}
