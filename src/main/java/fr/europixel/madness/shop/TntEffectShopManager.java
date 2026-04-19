package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerStats;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TntEffectShopManager {

    private final MadnessPlugin plugin;

    public TntEffectShopManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void ensurePlayer(Player player) {
        if (player == null) {
            return;
        }

        plugin.getDatabaseManager().ensureTntEffectPlayer(player.getUniqueId().toString(), "classic");
    }

    public void fillInventory(Player player, org.bukkit.inventory.Inventory inventory) {
        ensurePlayer(player);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop.effects");
        if (section == null) {
            return;
        }

        for (String effectId : section.getKeys(false)) {
            ConfigurationSection effectSection = section.getConfigurationSection(effectId);
            if (effectSection == null) {
                continue;
            }

            int slot = effectSection.getInt("slot", -1);
            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }

            inventory.setItem(slot, buildDisplayItem(player, effectId));
        }
    }

    public String getEffectIdBySlot(int slot) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop.effects");
        if (section == null) {
            return null;
        }

        for (String effectId : section.getKeys(false)) {
            ConfigurationSection effectSection = section.getConfigurationSection(effectId);
            if (effectSection == null) {
                continue;
            }

            if (effectSection.getInt("slot", -1) == slot) {
                return effectId;
            }
        }

        return null;
    }

    public boolean ownsEffect(Player player, String effectId) {
        ensurePlayer(player);
        Set<String> owned = plugin.getDatabaseManager().getOwnedTntEffects(player.getUniqueId().toString());
        return owned.contains(effectId.toLowerCase());
    }

    public String getSelectedEffect(Player player) {
        ensurePlayer(player);
        String selected = plugin.getDatabaseManager().getSelectedTntEffect(player.getUniqueId().toString());
        return selected == null ? "classic" : selected.toLowerCase();
    }

    public void setSelectedEffect(Player player, String effectId) {
        ensurePlayer(player);
        plugin.getDatabaseManager().setSelectedTntEffect(player.getUniqueId().toString(), effectId.toLowerCase());
    }

    public void handleClick(Player player, String effectId) {
        ensurePlayer(player);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop.effects." + effectId);
        if (section == null) {
            return;
        }

        effectId = effectId.toLowerCase();

        if (ownsEffect(player, effectId)) {
            setSelectedEffect(player, effectId);
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                    "messages.shop.tnt-effect-selected",
                    "&aTu as sélectionné l'effet TNT &f%effect%&a."
            )).replace("%effect%", getEffectName(effectId)));
            return;
        }

        int requiredLevel = section.getInt("required-level", 1);
        double price = section.getDouble("price", 0.0D);

        int playerLevel = 1;
        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
        if (stats != null) {
            playerLevel = stats.getLevel();
        }

        if (playerLevel < requiredLevel) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                    "messages.shop.upgrade-level-required",
                    "&cIl te faut le niveau &e%level% &cpour acheter cette amélioration."
            )).replace("%level%", String.valueOf(requiredLevel)));
            return;
        }

        if (plugin.getVaultEconomyHook() == null || !plugin.getVaultEconomyHook().isAvailable()) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                    "messages.shop.no-economy",
                    "&cEconomy is unavailable."
            )));
            return;
        }

        if (!plugin.getVaultEconomyHook().has(player, price)) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                    "messages.shop.not-enough",
                    "&cTu n'as pas assez de coins. &7(%price%)"
            )).replace("%price%", format(price)));
            return;
        }

        if (!plugin.getVaultEconomyHook().withdraw(player, price)) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                    "messages.shop.purchase-failed",
                    "&cAchat impossible."
            )));
            return;
        }

        plugin.getDatabaseManager().addOwnedTntEffect(player.getUniqueId().toString(), effectId);
        plugin.getDatabaseManager().setSelectedTntEffect(player.getUniqueId().toString(), effectId);

        player.sendMessage(ConfigUtil.color(plugin.getConfig().getString(
                        "messages.shop.tnt-effect-bought",
                        "&aTu as acheté l'effet TNT &f%effect% &apour &6%price% coins&a."
                ))
                .replace("%effect%", getEffectName(effectId))
                .replace("%price%", format(price)));
    }

    private ItemStack buildDisplayItem(Player player, String effectId) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop.effects." + effectId);

        Material material = ConfigUtil.getMaterial(section == null ? null : section.getString("material"), Material.TNT);
        int amount = section == null ? 1 : Math.max(1, section.getInt("amount", 1));

        ItemStack item = new ItemStack(material, amount, (short) (section == null ? 0 : section.getInt("data", 0)));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ConfigUtil.color(section == null ? effectId : section.getString("name", effectId)));

            List<String> lore = new ArrayList<String>();
            List<String> baseLore = section == null ? null : section.getStringList("lore");
            if (baseLore != null) {
                for (String line : baseLore) {
                    lore.add(ConfigUtil.color(line));
                }
            }

            int playerLevel = 1;
            PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
            if (stats != null) {
                playerLevel = stats.getLevel();
            }

            boolean owned = ownsEffect(player, effectId);
            boolean selected = getSelectedEffect(player).equalsIgnoreCase(effectId);

            lore.add(" ");
            lore.add(ConfigUtil.color("&7Niveau joueur: &b" + playerLevel));

            if (owned) {
                lore.add(ConfigUtil.color("&7Statut: &aDébloqué"));
                if (selected) {
                    lore.add(ConfigUtil.color("&7Sélection: &aActif"));
                } else {
                    lore.add(ConfigUtil.color("&eClique pour sélectionner"));
                }
            } else {
                int requiredLevel = section == null ? 1 : section.getInt("required-level", 1);
                double price = section == null ? 0.0D : section.getDouble("price", 0.0D);

                lore.add(ConfigUtil.color("&7Niveau requis: &e" + requiredLevel));
                lore.add(ConfigUtil.color("&7Prix: &6" + format(price) + " coins"));
                lore.add(ConfigUtil.color("&eClique pour acheter"));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private String getEffectName(String effectId) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("tnt-effects-shop.effects." + effectId);
        return ConfigUtil.color(section == null ? effectId : section.getString("name", effectId));
    }

    private String format(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}