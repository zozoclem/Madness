package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CooldownBarListener implements Listener {

    private final MadnessPlugin plugin;

    public CooldownBarListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                updateBar(player);
            }
        }, 1L);
    }

    public void updateBar(Player player) {
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            resetBar(player);
            return;
        }

        if (isTntItem(item) && plugin.getRechargeManager().isTntOnCooldown(player)) {
            player.setLevel(plugin.getRechargeManager().getRemainingTntSeconds(player));
            player.setExp(plugin.getRechargeManager().getTntProgress(player, 10));
            return;
        }

        if (isJetpackItem(item) && plugin.getRechargeManager().isJetpackOnCooldown(player)) {
            player.setLevel(plugin.getRechargeManager().getRemainingJetpackSeconds(player));
            player.setExp(plugin.getRechargeManager().getJetpackProgress(player, 60));
            return;
        }

        resetBar(player);
    }

    private void resetBar(Player player) {
        player.setLevel(0);
        player.setExp(0.0F);
    }

    private boolean isTntItem(ItemStack item) {
        if (item.getType() == Material.TNT) {
            return true;
        }

        if (item.getType() == Material.BARRIER) {
            return hasDisplayName(item, "§cTNT en recharge");
        }

        return false;
    }

    private boolean isJetpackItem(ItemStack item) {
        if (item.getType() == Material.FIREWORK) {
            return true;
        }

        if (item.getType() == Material.BARRIER) {
            return hasDisplayName(item, "§cJetpack en recharge");
        }

        return false;
    }

    private boolean hasDisplayName(ItemStack item, String expected) {
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        return expected.equals(meta.getDisplayName());
    }
}