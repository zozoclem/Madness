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

        plugin.getPlayerStatsManager().addDeath(victim);

        if (killer == null) {
            plugin.getSidebarManager().update(victim);
            return;
        }

        plugin.getPlayerStatsManager().addKill(killer);

        healInstant(killer);
        giveRewardItem(killer);
        plugin.getRechargeManager().resetTnt(killer);
        plugin.getRechargeManager().resetJetpack(killer);

        String actionBar = plugin.getConfig().getString("kill-rewards.actionbar", "&aVous avez tué &f%victim%");
        ActionBarUtil.sendActionBar(killer, ConfigUtil.color(actionBar.replace("%victim%", victim.getName())));

        plugin.getSidebarManager().update(killer);
        plugin.getSidebarManager().update(victim);
    }

    private void healInstant(Player killer) {
        double heal = plugin.getConfig().getDouble("kill-rewards.heal", 8.0D);
        double maxHealth = plugin.getConfig().getDouble("kill-rewards.max-health", 20.0D);

        double newHealth = killer.getHealth() + heal;
        if (newHealth > maxHealth) {
            newHealth = maxHealth;
        }

        killer.setHealth(newHealth);
        if (plugin.getConfig().getBoolean("kill-rewards.clear-fire", true)) {
            killer.setFireTicks(0);
        }
    }

    private void giveRewardItem(Player killer) {
        Material material = ConfigUtil.getMaterial(plugin.getConfig().getString("kill-rewards.item.material"), Material.GOLDEN_APPLE);
        int amount = plugin.getConfig().getInt("kill-rewards.item.amount", 1);
        killer.getInventory().addItem(new ItemStack(material, amount));
        killer.updateInventory();
    }
}
