package fr.europixel.madness;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class VoidDeathListener implements Listener {

    private final MadnessPlugin plugin;

    public VoidDeathListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            event.setCancelled(true);
            player.teleport(plugin.getLobbyManager().getLobbySpawn());
            return;
        }

        event.setCancelled(true);
        player.setHealth(0.0D);
    }
}