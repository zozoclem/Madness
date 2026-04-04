package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

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

        int tntMax = plugin.getConfig().getInt("tnt.recharge", 10);
        int jetpackMax = plugin.getConfig().getInt("jetpack.recharge", 60);

        if (isTntItem(item) && plugin.getRechargeManager().isTntOnCooldown(player)) {
            player.setLevel(plugin.getRechargeManager().getRemainingTntSeconds(player));
            player.setExp(plugin.getRechargeManager().getTntProgress(player, tntMax));
            return;
        }

        if (isJetpackItem(item) && plugin.getRechargeManager().isJetpackOnCooldown(player)) {
            player.setLevel(plugin.getRechargeManager().getRemainingJetpackSeconds(player));
            player.setExp(plugin.getRechargeManager().getJetpackProgress(player, jetpackMax));
            return;
        }

        resetBar(player);
    }

    private void resetBar(Player player) {
        player.setLevel(0);
        player.setExp(0.0F);
    }

    private boolean isTntItem(ItemStack item) {
        return ItemFactory.isSimilarKeyItem(item, "tnt")
                || ConfigUtil.sameDisplayName(item, MadnessPlugin.getInstance().getConfig().getString("cooldowns.tnt.name"));
    }

    private boolean isJetpackItem(ItemStack item) {
        return ItemFactory.isSimilarKeyItem(item, "jetpack")
                || ConfigUtil.sameDisplayName(item, MadnessPlugin.getInstance().getConfig().getString("cooldowns.jetpack.name"));
    }
}
