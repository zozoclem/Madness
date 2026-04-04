package fr.europixel.madness;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class JetpackListener implements Listener {

    private final MadnessPlugin plugin;

    public JetpackListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (!ItemFactory.isSimilarKeyItem(item, "jetpack")) {
            return;
        }

        if (plugin.getRechargeManager().isJetpackOnCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        double y = plugin.getConfig().getDouble("jetpack.y", 3.6D);

        Vector v = player.getLocation().getDirection();
        v.setY(y);

        player.setVelocity(v);

        plugin.getRechargeManager().startJetpackRecharge(player, plugin.getConfig().getInt("jetpack.recharge", 60));
        player.updateInventory();
    }
}
