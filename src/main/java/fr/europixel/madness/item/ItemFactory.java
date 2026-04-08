package fr.europixel.madness.item;

import fr.europixel.madness.utils.ConfigUtil;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemFactory {

    private static ConfigurationSection items() {
        MadnessPlugin plugin = MadnessPlugin.getInstance();
        return plugin == null ? null : plugin.getConfig().getConfigurationSection("items");
    }

    public static ItemStack createTntItem() {
        ConfigurationSection section = items() == null ? null : items().getConfigurationSection("tnt");
        return ConfigItemFactory.fromSection(section, Material.TNT, 1);
    }

    public static ItemStack createJetpackItem() {
        ConfigurationSection section = items() == null ? null : items().getConfigurationSection("jetpack");
        return ConfigItemFactory.fromSection(section, Material.FIREWORK, 1);
    }

    public static ItemStack createPlayAxe() {
        ConfigurationSection section = items() == null ? null : items().getConfigurationSection("play");
        return ConfigItemFactory.fromSection(section, Material.DIAMOND_AXE, 1);
    }

    public static ItemStack createEditKitItem() {
        ConfigurationSection section = items() == null ? null : items().getConfigurationSection("edit-kit");
        return ConfigItemFactory.fromSection(section, Material.BLAZE_ROD, 1);
    }

    public static ItemStack createShopItem() {
        ConfigurationSection section = items() == null ? null : items().getConfigurationSection("shop");
        return ConfigItemFactory.fromSection(section, Material.EMERALD, 1);
    }

    public static boolean isSimilarKeyItem(ItemStack item, String configKey) {
        if (item == null) {
            return false;
        }

        ConfigurationSection section = items() == null ? null : items().getConfigurationSection(configKey);
        if (section == null) {
            return false;
        }

        Material expected = ConfigUtil.getMaterial(section.getString("material"), item.getType());
        if (item.getType() != expected) {
            return false;
        }

        if (section.contains("name")) {
            return ConfigUtil.sameDisplayName(item, section.getString("name"));
        }

        return true;
    }
}