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

public class UpgradeShopManager {

    private final MadnessPlugin plugin;

    public UpgradeShopManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void ensurePlayer(Player player) {
        plugin.getDatabaseManager().ensureUpgradeShopPlayer(player.getUniqueId().toString());
    }

    public void fillInventory(Player player, org.bukkit.inventory.Inventory inventory) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("upgrade-shop.upgrades");
        if (section == null) {
            return;
        }

        for (String upgradeId : section.getKeys(false)) {
            ConfigurationSection upgradeSection = section.getConfigurationSection(upgradeId);
            if (upgradeSection == null) {
                continue;
            }

            int slot = upgradeSection.getInt("slot", -1);
            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }

            inventory.setItem(slot, buildDisplayItem(player, upgradeId));
        }
    }

    public String getUpgradeIdBySlot(int slot) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("upgrade-shop.upgrades");
        if (section == null) {
            return null;
        }

        for (String upgradeId : section.getKeys(false)) {
            ConfigurationSection upgradeSection = section.getConfigurationSection(upgradeId);
            if (upgradeSection == null) {
                continue;
            }

            if (upgradeSection.getInt("slot", -1) == slot) {
                return upgradeId;
            }
        }

        return null;
    }

    public int getUpgradeLevel(Player player, String upgradeId) {
        ensurePlayer(player);
        return plugin.getDatabaseManager().getUpgradeLevel(player.getUniqueId().toString(), upgradeId);
    }

    public void setUpgradeLevel(Player player, String upgradeId, int level) {
        ensurePlayer(player);
        plugin.getDatabaseManager().setUpgradeLevel(player.getUniqueId().toString(), upgradeId, level);
    }

    public int getMaxLevel(String upgradeId) {
        ConfigurationSection section = getUpgradeSection(upgradeId);
        return section == null ? 0 : Math.max(0, section.getInt("max-level", 0));
    }

    public boolean isMaxed(Player player, String upgradeId) {
        return getUpgradeLevel(player, upgradeId) >= getMaxLevel(upgradeId);
    }

    public void handleClick(Player player, String upgradeId) {
        ConfigurationSection section = getUpgradeSection(upgradeId);
        if (section == null) {
            return;
        }

        ensurePlayer(player);

        int currentLevel = getUpgradeLevel(player, upgradeId);
        int nextLevel = currentLevel + 1;
        int maxLevel = getMaxLevel(upgradeId);

        if (nextLevel > maxLevel) {
            player.sendMessage(ConfigUtil.color(
                    plugin.getConfig().getString(
                            "messages.shop.upgrade-maxed",
                            "&aThis upgrade is already maxed."
                    )
            ));
            return;
        }

        ConfigurationSection tierSection = section.getConfigurationSection("tiers." + nextLevel);
        if (tierSection == null) {
            player.sendMessage(ConfigUtil.color("&cMissing tier for " + upgradeId + " level " + nextLevel));
            return;
        }

        int requiredLevel = tierSection.getInt("required-level", 1);
        double price = tierSection.getDouble("price", 0.0D);

        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
        int playerLevel = stats == null ? 1 : stats.getLevel();

        if (playerLevel < requiredLevel) {
            player.sendMessage(
                    ConfigUtil.color(
                            plugin.getConfig().getString(
                                    "messages.shop.upgrade-level-required",
                                    "&cYou need level &e%level% &cto unlock this upgrade."
                            )
                    ).replace("%level%", String.valueOf(requiredLevel))
            );
            return;
        }

        if (plugin.getVaultEconomyHook() == null || !plugin.getVaultEconomyHook().isAvailable()) {
            player.sendMessage(ConfigUtil.color(
                    plugin.getConfig().getString(
                            "messages.shop.no-economy",
                            "&cEconomy is unavailable."
                    )
            ));
            return;
        }

        if (!plugin.getVaultEconomyHook().has(player, price)) {
            player.sendMessage(
                    ConfigUtil.color(
                            plugin.getConfig().getString(
                                    "messages.shop.not-enough",
                                    "&cYou do not have enough coins. &7(%price%)"
                            )
                    ).replace("%price%", format(price))
            );
            return;
        }

        if (!plugin.getVaultEconomyHook().withdraw(player, price)) {
            player.sendMessage(ConfigUtil.color(
                    plugin.getConfig().getString(
                            "messages.shop.purchase-failed",
                            "&cPurchase failed."
                    )
            ));
            return;
        }

        setUpgradeLevel(player, upgradeId, nextLevel);

        player.sendMessage(
                ConfigUtil.color(
                                plugin.getConfig().getString(
                                        "messages.shop.upgrade-bought",
                                        "&aYou purchased &f%upgrade% &alevel &e%level% &afor &6%price% coins&a."
                                )
                        )
                        .replace("%upgrade%", getPrettyName(upgradeId))
                        .replace("%level%", String.valueOf(nextLevel))
                        .replace("%price%", format(price))
        );
    }

    public double getTntRechargeSeconds(Player player) {
        return getComputedValue(player, "tnt_cooldown");
    }

    public double getJetpackRechargeSeconds(Player player) {
        return getComputedValue(player, "jetpack_cooldown");
    }

    public int getGoldenAppleBonus(Player player) {
        return (int) Math.round(getComputedValue(player, "golden_apples"));
    }

    private double getComputedValue(Player player, String upgradeId) {
        ConfigurationSection section = getUpgradeSection(upgradeId);
        if (section == null) {
            return 0.0D;
        }

        double baseValue = section.getDouble("base-value", 0.0D);
        double perLevel = section.getDouble("value-per-level", 0.0D);
        int level = getUpgradeLevel(player, upgradeId);

        double value = baseValue + (perLevel * level);

        double minValue = section.getDouble("min-value", Double.NEGATIVE_INFINITY);
        double maxValue = section.getDouble("max-value", Double.POSITIVE_INFINITY);

        if (value < minValue) value = minValue;
        if (value > maxValue) value = maxValue;

        return value;
    }

    private ItemStack buildDisplayItem(Player player, String upgradeId) {
        ConfigurationSection section = getUpgradeSection(upgradeId);
        Material material = ConfigUtil.getMaterial(section == null ? null : section.getString("material"), Material.REDSTONE);
        int amount = section == null ? 1 : Math.max(1, section.getInt("amount", 1));
        ItemStack item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section == null ? upgradeId : section.getString("name", upgradeId);
            meta.setDisplayName(ConfigUtil.color(name));

            List<String> lore = new ArrayList<String>();
            List<String> baseLore = section == null ? null : section.getStringList("lore");

            if (baseLore != null) {
                for (String line : baseLore) {
                    lore.add(ConfigUtil.color(line));
                }
            }

            int upgradeLevel = getUpgradeLevel(player, upgradeId);
            int nextLevel = upgradeLevel + 1;
            int maxLevel = getMaxLevel(upgradeId);

            int playerLevel = 1;
            PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
            if (stats != null) {
                playerLevel = stats.getLevel();
            }

            lore.add(" ");
            lore.add(ConfigUtil.color("&7Your level: &b" + playerLevel));

            if (nextLevel > maxLevel) {
                lore.add(ConfigUtil.color("&aMAX LEVEL"));
                lore.add(ConfigUtil.color("&7Upgrade: &e" + upgradeLevel + "&7/&e" + maxLevel));
            } else {
                ConfigurationSection tier = section.getConfigurationSection("tiers." + nextLevel);

                int requiredLevel = tier.getInt("required-level", 1);
                double price = tier.getDouble("price", 0.0D);

                lore.add(ConfigUtil.color("&7Required level: &e" + requiredLevel));
                lore.add(ConfigUtil.color("&7Cost: &6" + format(price) + " coins"));
                lore.add(ConfigUtil.color("&7Progress: &e" + upgradeLevel + "&7/&e" + maxLevel));
                lore.add(ConfigUtil.color("&7Next level: &e" + nextLevel));
                lore.add(ConfigUtil.color("&eClick to upgrade"));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ConfigurationSection getUpgradeSection(String upgradeId) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("upgrade-shop.upgrades");
        return section == null ? null : section.getConfigurationSection(upgradeId);
    }

    private String getPrettyName(String upgradeId) {
        ConfigurationSection section = getUpgradeSection(upgradeId);
        return section == null ? upgradeId : ConfigUtil.color(section.getString("name", upgradeId));
    }

    private String format(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}