package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CooldownItemFactory {

    public static ItemStack createBarrier(String path, int seconds) {
        MadnessPlugin plugin = MadnessPlugin.getInstance();
        ConfigurationSection section = plugin == null ? null : plugin.getConfig().getConfigurationSection(path);

        ItemStack item = ConfigItemFactory.fromSection(section, Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
            String template = section != null ? section.getString("remaining-line", "&7Temps restant: &c%seconds%s") : "&7Temps restant: &c%seconds%s";
            lore.add(ConfigUtil.color(template.replace("%seconds%", String.valueOf(seconds))));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
