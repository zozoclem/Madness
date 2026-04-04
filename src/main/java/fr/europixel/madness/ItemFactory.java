package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class ItemFactory {

    private ItemFactory() {
    }

    public static ItemStack createTntItem() {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lInstant TNT");
            meta.setLore(Arrays.asList(
                    "",
                    " §7Une TNT instantanée",
                    " §7Recharge: §f10s",
                    ""
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createJetpackItem() {
        ItemStack item = new ItemStack(Material.FIREWORK, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§lJetpack");
            meta.setLore(Arrays.asList(
                    "",
                    " §7Propulsion vers le haut",
                    " §7Recharge: §f60s",
                    ""
            ));
            item.setItemMeta(meta);
        }

        return item;
    }
}