package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class BlockShopListener implements Listener {

    private final MadnessPlugin plugin;

    public BlockShopListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if (ShopCategoryMenu.getTitle(plugin).equals(title)) {
            event.setCancelled(true);

            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) {
                return;
            }

            int blocksSlot = plugin.getConfig().getInt("shop-categories.blocks.slot", 10);
            int upgradesSlot = plugin.getConfig().getInt("shop-categories.upgrades.slot", 13);
            int tntEffectsSlot = plugin.getConfig().getInt("shop-categories.tnt-effects.slot", 16);

            if (rawSlot == blocksSlot) {
                BlockShopMenu.open(plugin, player);
                return;
            }

            if (rawSlot == upgradesSlot) {
                UpgradeShopMenu.open(plugin, player);
                return;
            }

            if (rawSlot == tntEffectsSlot) {
                TntEffectShopMenu.open(plugin, player);
            }
            return;
        }

        if (BlockShopMenu.getTitle(plugin).equals(title)) {
            event.setCancelled(true);

            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) {
                return;
            }

            String optionId = plugin.getBlockShopManager().getOptionIdBySlot(rawSlot);
            if (optionId == null) {
                return;
            }

            plugin.getBlockShopManager().handleClick(player, optionId);
            BlockShopMenu.open(plugin, player);
            return;
        }

        if (UpgradeShopMenu.getTitle(plugin).equals(title)) {
            event.setCancelled(true);

            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) {
                return;
            }

            String upgradeId = plugin.getUpgradeShopManager().getUpgradeIdBySlot(rawSlot);
            if (upgradeId == null) {
                return;
            }

            plugin.getUpgradeShopManager().handleClick(player, upgradeId);
            UpgradeShopMenu.open(plugin, player);
            return;
        }

        if (TntEffectShopMenu.getTitle(plugin).equals(title)) {
            event.setCancelled(true);

            int rawSlot = event.getRawSlot();
            if (rawSlot < 0 || rawSlot >= event.getView().getTopInventory().getSize()) {
                return;
            }

            String effectId = plugin.getTntEffectShopManager().getEffectIdBySlot(rawSlot);
            if (effectId == null) {
                return;
            }

            plugin.getTntEffectShopManager().handleClick(player, effectId);
            TntEffectShopMenu.open(plugin, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        String title = event.getView().getTitle();

        if (!ShopCategoryMenu.getTitle(plugin).equals(title)
                && !BlockShopMenu.getTitle(plugin).equals(title)
                && !UpgradeShopMenu.getTitle(plugin).equals(title)
                && !TntEffectShopMenu.getTitle(plugin).equals(title)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        player.setItemOnCursor(new ItemStack(Material.AIR));

        if (plugin.getPlayerModeManager().isInLobby(player)) {
            plugin.getLobbyManager().giveLobbyItems(player);
            player.getInventory().setHeldItemSlot(Math.max(0, Math.min(8, plugin.getConfig().getInt("lobby.selected-slot", 4))));
            player.updateInventory();
        }
    }
}