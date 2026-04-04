package fr.europixel.madness;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ArenaInventoryLockListener implements Listener {

    private final MadnessPlugin plugin;

    public ArenaInventoryLockListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // ignore l'EditKit
        if (event.getView() != null && EditKitMenu.TITLE.equals(event.getView().getTitle())) {
            return;
        }

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            return;
        }

        // bloque tout mouvement d'inventaire en arène
        event.setCancelled(true);

        // sécurité en plus
        if (event.isShiftClick()
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.DROP
                || event.getClick() == ClickType.CONTROL_DROP) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // ignore l'EditKit
        if (event.getView() != null && EditKitMenu.TITLE.equals(event.getView().getTitle())) {
            return;
        }

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            return;
        }

        event.setCancelled(true);
    }
}