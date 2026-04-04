package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillListener implements Listener {

    private final MadnessPlugin plugin;

    public KillListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        healInstant(killer);
        giveGoldenApple(killer);
        plugin.getRechargeManager().resetTntCooldown(killer);
        ActionBarUtil.sendActionBar(killer, "§aVous avez tué §f" + victim.getName());
    }

    private void healInstant(Player killer) {
        double newHealth = killer.getHealth() + 8.0D;
        if (newHealth > 20.0D) {
            newHealth = 20.0D;
        }

        killer.setHealth(newHealth);
        killer.setFireTicks(0);
    }

    private void giveGoldenApple(Player killer) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE, 1);
        killer.getInventory().addItem(item);
        killer.updateInventory();
    }
}