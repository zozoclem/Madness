package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditKitMenu {

    public static String getTitle(MadnessPlugin plugin) {
        return ConfigUtil.color(plugin.getConfig().getString("editkit-menu.title", "&eEditKit"));
    }

    public static void open(MadnessPlugin plugin, Player player) {
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        ConfigurationSection menu = plugin.getConfig().getConfigurationSection("editkit-menu");
        int size = menu == null ? 27 : menu.getInt("size", 27);
        Inventory inv = Bukkit.createInventory(null, size, getTitle(plugin));

        ItemStack[] hotbar = plugin.getPlayerStatsManager().getHotbar(player);
        if (hotbar == null) {
            hotbar = plugin.getKitManager().createDefaultHotbar();
        }

        for (int i = 0; i < 9 && i < size; i++) {
            inv.setItem(i, hotbar[i] == null ? null : hotbar[i].clone());
        }

        ItemStack filler = ConfigItemFactory.fromSection(menu == null ? null : menu.getConfigurationSection("filler"), Material.STAINED_GLASS_PANE, 1);
        for (int i = 9; i < size; i++) {
            inv.setItem(i, filler.clone());
        }

        int saveSlot = menu == null ? 22 : menu.getInt("save.slot", 22);
        int cancelSlot = menu == null ? 26 : menu.getInt("cancel.slot", 26);

        if (saveSlot >= 0 && saveSlot < size) {
            inv.setItem(saveSlot, ConfigItemFactory.fromSection(menu == null ? null : menu.getConfigurationSection("save.item"), Material.STAINED_CLAY, 1));
        }

        if (cancelSlot >= 0 && cancelSlot < size) {
            inv.setItem(cancelSlot, ConfigItemFactory.fromSection(menu == null ? null : menu.getConfigurationSection("cancel.item"), Material.STAINED_CLAY, 1));
        }

        player.openInventory(inv);
    }
}
