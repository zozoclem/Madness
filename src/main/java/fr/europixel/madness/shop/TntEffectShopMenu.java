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

public class TntEffectShopMenu {

    public static String getTitle(MadnessPlugin plugin) {
        return ConfigUtil.color(plugin.getConfig().getString("tnt-effects-shop.title", "&6Shop - Effets TNT"));
    }

    public static void open(MadnessPlugin plugin, Player player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop");
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

        if (plugin.getTntEffectShopManager() != null) {
            plugin.getTntEffectShopManager().fillInventory(player, inventory);
        }

        player.openInventory(inventory);
    }
}