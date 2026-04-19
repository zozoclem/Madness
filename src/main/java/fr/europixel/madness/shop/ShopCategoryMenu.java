package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.item.ConfigItemFactory;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopCategoryMenu {

    public static String getTitle(MadnessPlugin plugin) {
        return ConfigUtil.color(plugin.getConfig().getString("shop-categories.title", "&6Shop"));
    }

    public static void open(MadnessPlugin plugin, Player player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shop-categories");
        int size = section == null ? 27 : section.getInt("size", 27);

        Inventory inventory = Bukkit.createInventory(null, size, getTitle(plugin));

        ItemStack filler = ConfigItemFactory.fromSection(
                section == null ? null : section.getConfigurationSection("filler"),
                Material.STAINED_GLASS_PANE,
                1
        );

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, filler.clone());
        }

        inventory.setItem(
                section == null ? 10 : section.getInt("blocks.slot", 10),
                ConfigItemFactory.fromSection(
                        section == null ? null : section.getConfigurationSection("blocks.item"),
                        Material.SANDSTONE,
                        1
                )
        );

        inventory.setItem(
                section == null ? 13 : section.getInt("upgrades.slot", 13),
                ConfigItemFactory.fromSection(
                        section == null ? null : section.getConfigurationSection("upgrades.item"),
                        Material.REDSTONE,
                        1
                )
        );

        inventory.setItem(
                section == null ? 16 : section.getInt("tnt-effects.slot", 16),
                ConfigItemFactory.fromSection(
                        section == null ? null : section.getConfigurationSection("tnt-effects.item"),
                        Material.TNT,
                        1
                )
        );

        player.openInventory(inventory);
    }
}