package fr.europixel.madness.bar;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CooldownBarListener implements Listener {

    private final MadnessPlugin plugin;

    public CooldownBarListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateBar(Player player) {
        if (player == null || plugin.getRechargeManager() == null) {
            return;
        }

        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            resetBar(player);
            return;
        }

        double tntMax = plugin.getUpgradeShopManager() == null
                ? plugin.getConfig().getDouble("tnt.recharge", 10.0D)
                : plugin.getUpgradeShopManager().getTntRechargeSeconds(player);

        double jetpackMax = plugin.getUpgradeShopManager() == null
                ? plugin.getConfig().getDouble("jetpack.recharge", 60.0D)
                : plugin.getUpgradeShopManager().getJetpackRechargeSeconds(player);

        int heldSlot = player.getInventory().getHeldItemSlot();

        Integer tntSlot = plugin.getRechargeManager().getTntSlot(player);
        if (tntSlot != null && heldSlot == tntSlot && plugin.getRechargeManager().isTntOnCooldown(player)) {
            double remaining = plugin.getRechargeManager().getRemainingTntSeconds(player);
            player.setLevel((int) Math.ceil(remaining));
            player.setExp(plugin.getRechargeManager().getTntProgress(player, tntMax));
            return;
        }

        Integer jetpackSlot = plugin.getRechargeManager().getJetpackSlot(player);
        if (jetpackSlot != null && heldSlot == jetpackSlot && plugin.getRechargeManager().isJetpackOnCooldown(player)) {
            double remaining = plugin.getRechargeManager().getRemainingJetpackSeconds(player);
            player.setLevel((int) Math.ceil(remaining));
            player.setExp(plugin.getRechargeManager().getJetpackProgress(player, jetpackMax));
            return;
        }

        if (ItemFactory.isSimilarKeyItem(item, "tnt") && plugin.getRechargeManager().isTntOnCooldown(player)) {
            double remaining = plugin.getRechargeManager().getRemainingTntSeconds(player);
            player.setLevel((int) Math.ceil(remaining));
            player.setExp(plugin.getRechargeManager().getTntProgress(player, tntMax));
            return;
        }

        if (ItemFactory.isSimilarKeyItem(item, "jetpack") && plugin.getRechargeManager().isJetpackOnCooldown(player)) {
            double remaining = plugin.getRechargeManager().getRemainingJetpackSeconds(player);
            player.setLevel((int) Math.ceil(remaining));
            player.setExp(plugin.getRechargeManager().getJetpackProgress(player, jetpackMax));
            return;
        }

        resetBar(player);
    }

    public void resetBar(Player player) {
        if (player == null) {
            return;
        }

        player.setLevel(0);
        player.setExp(0.0F);
    }
}