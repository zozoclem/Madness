package fr.europixel.madness.item;

import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ConfigItemFactory {

    private ConfigItemFactory() {
    }

    public static ItemStack fromSection(ConfigurationSection section, Material fallbackMaterial, int fallbackAmount) {
        if (section == null) {
            return new ItemStack(fallbackMaterial, fallbackAmount);
        }

        Material material = ConfigUtil.getMaterial(section.getString("material"), fallbackMaterial);
        int amount = Math.max(1, section.getInt("amount", fallbackAmount));
        short data = (short) section.getInt("data", 0);

        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (section.contains("name")) {
                meta.setDisplayName(ConfigUtil.color(section.getString("name", "")));
            }

            List<String> lore = section.getStringList("lore");
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(ConfigUtil.color(lore));
            }

            item.setItemMeta(meta);
        }

        ConfigurationSection enchants = section.getConfigurationSection("enchants");
        if (enchants != null) {
            for (String key : enchants.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                if (enchantment != null) {
                    item.addUnsafeEnchantment(enchantment, enchants.getInt(key, 1));
                }
            }
        }

        return item;
    }
}
