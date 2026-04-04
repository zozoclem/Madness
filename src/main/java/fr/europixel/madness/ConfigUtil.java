package fr.europixel.madness;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public final class ConfigUtil {

    private ConfigUtil() {
    }

    public static String color(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> lines) {
        List<String> colored = new ArrayList<String>();
        if (lines == null) {
            return colored;
        }

        for (String line : lines) {
            colored.add(color(line));
        }
        return colored;
    }

    public static Material getMaterial(String name, Material fallback) {
        if (name == null || name.trim().isEmpty()) {
            return fallback;
        }

        Material material = Material.matchMaterial(name);
        return material == null ? fallback : material;
    }

    public static boolean sameDisplayName(org.bukkit.inventory.ItemStack item, String expected) {
        if (item == null || expected == null || !item.hasItemMeta()) {
            return false;
        }

        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && color(expected).equals(meta.getDisplayName());
    }

    public static String getString(ConfigurationSection section, String path, String fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getString(path, fallback);
    }

    public static List<String> getStringList(ConfigurationSection section, String path) {
        if (section == null) {
            return new ArrayList<String>();
        }
        return section.getStringList(path);
    }
}
