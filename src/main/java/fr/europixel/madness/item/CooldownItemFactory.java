package fr.europixel.madness.item;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CooldownItemFactory {

    public static ItemStack createBarrier(String path, int seconds) {
        MadnessPlugin plugin = MadnessPlugin.getInstance();

        Material material = ConfigUtil.getMaterial(
                plugin.getConfig().getString(path + ".material"),
                Material.BARRIER
        );

        int amount = plugin.getConfig().getInt(path + ".amount", 1);

        ItemStack item = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = plugin.getConfig().getString(path + ".name", "&cRecharging");
            meta.setDisplayName(ConfigUtil.color(name));

            List<String> lore = new ArrayList<String>();
            List<String> baseLore = plugin.getConfig().getStringList(path + ".lore");
            if (baseLore != null) {
                for (String line : baseLore) {
                    lore.add(ConfigUtil.color(line));
                }
            }

            String remainingLine = plugin.getConfig().getString(path + ".remaining-line", "&7Time left: &c%seconds%s");
            lore.add(ConfigUtil.color(remainingLine.replace("%seconds%", String.valueOf(seconds))));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}