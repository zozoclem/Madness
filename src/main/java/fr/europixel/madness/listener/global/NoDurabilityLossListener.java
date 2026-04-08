package fr.europixel.madness.listener.global;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class NoDurabilityLossListener implements Listener {

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        event.setCancelled(true);
    }
}