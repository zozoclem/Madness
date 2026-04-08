package fr.europixel.madness.listener.global;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class DamageProtectionListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        ConfigurationSection section = MadnessPlugin.getInstance().getConfig().getConfigurationSection("protections.damage-cancelled");
        if (section == null) {
            return;
        }

        List<String> causes = section.getStringList("causes");
        for (String causeName : causes) {
            try {
                EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.valueOf(causeName.toUpperCase());
                if (event.getCause() == cause) {
                    event.setCancelled(true);
                    return;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
