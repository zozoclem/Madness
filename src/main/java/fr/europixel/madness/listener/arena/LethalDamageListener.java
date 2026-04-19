package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class LethalDamageListener implements Listener {

    private final MadnessPlugin plugin;

    public LethalDamageListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLethalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();

        if (!plugin.getPlayerModeManager().isInArena(victim)) {
            return;
        }

        if (plugin.getArenaEliminationManager().isProcessing(victim)) {
            event.setCancelled(true);
            return;
        }

        double finalDamage = event.getFinalDamage();
        if (victim.getHealth() - finalDamage > 0.0D) {
            return;
        }

        event.setCancelled(true);

        Player killer = resolveKiller(victim, event);
        plugin.getArenaEliminationManager().eliminate(victim, killer);
    }

    private Player resolveKiller(Player victim, EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

            if (damager instanceof Player) {
                return (Player) damager;
            }

            if (damager instanceof Arrow) {
                ProjectileSource source = ((Arrow) damager).getShooter();
                if (source instanceof Player) {
                    return (Player) source;
                }
            }
        }

        Player killer = victim.getKiller();
        if (killer != null && killer.isOnline() && !killer.getUniqueId().equals(victim.getUniqueId())) {
            return killer;
        }

        return null;
    }
}
