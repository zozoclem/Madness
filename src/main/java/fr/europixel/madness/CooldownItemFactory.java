package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CooldownItemFactory {

    public static ItemStack createBarrier(String name, int seconds) {
        return createBarrier(name, "§7Recharge en cours...", seconds);
    }

    public static ItemStack createBarrier(String name, String description, int seconds) {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);

            List<String> lore = new ArrayList<String>();
            lore.add(description);
            lore.add("§7Temps restant: §c" + seconds + "s");
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }
}