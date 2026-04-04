package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EditKitListener implements Listener {

    private final MadnessPlugin plugin;

    public EditKitListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        if (!EditKitMenu.getTitle(plugin).equals(event.getView().getTitle())) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory top = event.getView().getTopInventory();
        int rawSlot = event.getRawSlot();

        if (rawSlot < 0) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick()
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.DROP
                || event.getClick() == ClickType.CONTROL_DROP
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR
                || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot >= top.getSize()) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot >= 0 && rawSlot <= 8) {
            event.setCancelled(false);
            return;
        }

        int saveSlot = plugin.getConfig().getInt("editkit-menu.save.slot", 22);
        int cancelSlot = plugin.getConfig().getInt("editkit-menu.cancel.slot", 26);

        if (rawSlot == saveSlot) {
            event.setCancelled(true);

            ItemStack[] edited = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                ItemStack item = top.getItem(i);
                edited[i] = item == null ? null : item.clone();
            }

            if (!isValidHotbar(edited)) {
                player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.editkit.invalid", "&cHotbar invalide.")));
                player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.editkit.invalid-details", "&7Vous devez garder tous les items du kit.")));
                return;
            }

            plugin.getPlayerStatsManager().setHotbar(player, edited);
            plugin.getPlayerStatsManager().savePlayer(player);

            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.editkit.saved", "&aVotre hotbar a été sauvegardée.")));
            player.closeInventory();
            return;
        }

        if (rawSlot == cancelSlot) {
            event.setCancelled(true);
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.editkit.cancelled", "&cModification annulée.")));
            player.closeInventory();
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        if (!EditKitMenu.getTitle(plugin).equals(event.getView().getTitle())) {
            return;
        }

        Inventory top = event.getView().getTopInventory();

        for (int slot : event.getRawSlots()) {
            if (slot < 0 || slot >= top.getSize() || slot > 8) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) {
            return;
        }

        if (!EditKitMenu.getTitle(plugin).equals(event.getView().getTitle())) {
            return;
        }

        Player player = (Player) event.getPlayer();

        player.setItemOnCursor(new ItemStack(Material.AIR));
        plugin.getLobbyManager().giveLobbyItems(player);
        player.getInventory().setHeldItemSlot(Math.max(0, Math.min(8, plugin.getConfig().getInt("lobby.selected-slot", 4))));
        player.updateInventory();
    }

    private boolean isValidHotbar(ItemStack[] hotbar) {
        Map<Material, Integer> currentCounts = count(hotbar);
        Map<Material, Integer> requiredCounts = count(plugin.getKitManager().createDefaultHotbar());

        for (Map.Entry<Material, Integer> entry : requiredCounts.entrySet()) {
            Integer current = currentCounts.get(entry.getKey());
            if (current == null || current < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    private Map<Material, Integer> count(ItemStack[] items) {
        Map<Material, Integer> counts = new HashMap<Material, Integer>();

        if (items == null) {
            return counts;
        }

        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }

            Material type = item.getType();
            Integer amount = counts.get(type);
            if (amount == null) {
                amount = 0;
            }

            counts.put(type, amount + item.getAmount());
        }

        return counts;
    }
}
