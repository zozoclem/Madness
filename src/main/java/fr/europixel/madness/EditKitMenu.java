package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditKitMenu {

    public static final String TITLE = "§eEditKit";

    public static void open(MadnessPlugin plugin, Player player) {
        // vide le curseur et l'inventaire joueur temporairement
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        ItemStack[] hotbar = plugin.getPlayerStatsManager().getHotbar(player);
        if (hotbar == null) {
            hotbar = plugin.getKitManager().createDefaultHotbar();
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, hotbar[i] == null ? null : hotbar[i].clone());
        }

        for (int i = 9; i < 27; i++) {
            if (i == 22 || i == 26) {
                continue;
            }
            inv.setItem(i, createGlass());
        }

        inv.setItem(22, createSaveItem());
        inv.setItem(26, createCancelItem());

        player.openInventory(inv);
    }

    private static ItemStack createSaveItem() {
        ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lSAUVEGARDER");
            List<String> lore = new ArrayList<String>();
            lore.add("§7Cliquez pour sauvegarder");
            lore.add("§7votre hotbar.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static ItemStack createCancelItem() {
        ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lANNULER");
            List<String> lore = new ArrayList<String>();
            lore.add("§7Cliquez pour annuler");
            lore.add("§7les modifications.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static ItemStack createGlass() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }

        return item;
    }
}