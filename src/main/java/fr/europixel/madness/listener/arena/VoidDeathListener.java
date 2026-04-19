package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
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

        Player victim = (Player) event.getEntity();
        event.setCancelled(true);

        if (!plugin.getPlayerModeManager().isInArena(victim)) {
            if (plugin.getLastDamagerManager() != null) {
                plugin.getLastDamagerManager().clear(victim);
            }
            plugin.getLobbyManager().sendToLobby(victim);
            return;
        }

        if (plugin.getArenaEliminationManager().isProcessing(victim)) {
            return;
        }

        Player killer = plugin.getLastDamagerManager() == null
                ? null
                : plugin.getLastDamagerManager().getLastDamager(victim);

        plugin.getArenaEliminationManager().eliminate(victim, killer);
    }
}