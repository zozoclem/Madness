package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class CooldownItemFactory {

    private CooldownItemFactory() {
    }

    public static ItemStack createTntCooldownItem(int seconds) {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lInstant TNT §8- §7Cooldown");
            meta.setLore(Arrays.asList(
                    "",
                    " §7Disponible dans: §f" + seconds + "s",
                    ""
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createJetpackCooldownItem(int seconds) {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§lJetpack §8- §7Cooldown");
            meta.setLore(Arrays.asList(
                    "",
                    " §7Disponible dans: §f" + seconds + "s",
                    ""
            ));
            item.setItemMeta(meta);
        }

        return item;
    }
}