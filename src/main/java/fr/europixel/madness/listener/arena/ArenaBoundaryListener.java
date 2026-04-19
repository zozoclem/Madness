package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ArenaBoundaryListener implements Listener {

    private final MadnessPlugin plugin;

    public ArenaBoundaryListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player victim = event.getPlayer();

        if (!plugin.getPlayerModeManager().isInArena(victim)) {
            return;
        }

        if (!plugin.getArenaManager().hasArenaBounds()) {
            return;
        }

        if (plugin.getArenaManager().isInsideArena(event.getTo())) {
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