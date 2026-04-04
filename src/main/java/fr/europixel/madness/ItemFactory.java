package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public static ItemStack createTntItem() {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lTNT");

            List<String> lore = new ArrayList<String>();
            lore.add("§7Utilisez pour vous propulser.");
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createJetpackItem() {
        ItemStack item = new ItemStack(Material.FIREWORK, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§b§lJetpack");

            List<String> lore = new ArrayList<String>();
            lore.add("§7Utilisez pour vous déplacer.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createPlayAxe() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lJouer");

            List<String> lore = new ArrayList<String>();
            lore.add("§7Cliquez pour rejoindre l'arène.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createEditKitItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§lEditKit");

            List<String> lore = new ArrayList<String>();
            lore.add("§7Cliquez pour modifier");
            lore.add("§7votre hotbar.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}