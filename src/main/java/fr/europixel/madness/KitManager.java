package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitManager {

    private final MadnessPlugin plugin;

    public KitManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void giveKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ConfigurationSection kitSection = plugin.getConfig().getConfigurationSection("kit");
        ConfigurationSection armorSection = kitSection == null ? null : kitSection.getConfigurationSection("armor");

        ItemStack boots = ConfigItemFactory.fromSection(armorSection == null ? null : armorSection.getConfigurationSection("boots"), Material.IRON_BOOTS, 1);
        ItemStack leggings = ConfigItemFactory.fromSection(armorSection == null ? null : armorSection.getConfigurationSection("leggings"), Material.IRON_LEGGINGS, 1);
        ItemStack chestplate = ConfigItemFactory.fromSection(armorSection == null ? null : armorSection.getConfigurationSection("chestplate"), Material.IRON_CHESTPLATE, 1);
        ItemStack helmet = ConfigItemFactory.fromSection(armorSection == null ? null : armorSection.getConfigurationSection("helmet"), Material.IRON_HELMET, 1);

        player.getInventory().setArmorContents(new ItemStack[] {
                boots, leggings, chestplate, helmet
        });

        ItemStack[] defaultHotbar = createDefaultHotbar();
        for (int slot = 0; slot < 9; slot++) {
            player.getInventory().setItem(slot, defaultHotbar[slot]);
        }

        applySavedHotbar(player);

        double health = plugin.getConfig().getDouble("player-reset.health", 20.0D);
        int food = plugin.getConfig().getInt("player-reset.food", 20);
        int heldSlot = plugin.getConfig().getInt("kit.selected-slot", 0);

        player.setHealth(Math.min(20.0D, health));
        player.setFoodLevel(food);
        player.setFireTicks(0);
        player.setLevel(0);
        player.setExp(0.0F);
        player.getInventory().setHeldItemSlot(Math.max(0, Math.min(8, heldSlot)));
        player.updateInventory();
    }

    private void applySavedHotbar(Player player) {
        ItemStack[] saved = plugin.getPlayerStatsManager().getHotbar(player);
        if (saved == null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, saved[i] == null ? null : saved[i].clone());
        }
    }

    public ItemStack[] createDefaultHotbar() {
        ItemStack[] hotbar = new ItemStack[9];
        ConfigurationSection hotbarSection = plugin.getConfig().getConfigurationSection("kit.hotbar");

        for (int i = 0; i < 9; i++) {
            ConfigurationSection slotSection = hotbarSection == null ? null : hotbarSection.getConfigurationSection(String.valueOf(i));
            hotbar[i] = ConfigItemFactory.fromSection(slotSection, Material.AIR, 1);
            if (hotbar[i].getType() == Material.AIR) {
                hotbar[i] = null;
            }
        }

        return hotbar;
    }
}
