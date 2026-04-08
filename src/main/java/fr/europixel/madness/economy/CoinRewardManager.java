package fr.europixel.madness.economy;

import fr.europixel.madness.utils.ConfigUtil;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoinRewardManager {

    private final MadnessPlugin plugin;

    public CoinRewardManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("coins.enabled", false)
                && plugin.getVaultEconomyHook() != null
                && plugin.getVaultEconomyHook().isAvailable();
    }

    public double getBaseReward() {
        return plugin.getConfig().getDouble("coins.reward-per-kill", 10.0D);
    }

    public int getRounding() {
        int rounding = plugin.getConfig().getInt("coins.rounding", 2);
        return Math.max(0, rounding);
    }

    public double getBestMultiplier(Player player) {
        double best = plugin.getConfig().getDouble("coins.multipliers.default", 1.0D);

        ConfigurationSection listSection = plugin.getConfig().getConfigurationSection("coins.multipliers.permission-list");
        if (listSection == null) {
            return best;
        }

        for (String key : listSection.getKeys(false)) {
            ConfigurationSection entry = listSection.getConfigurationSection(key);
            if (entry == null) continue;

            String permission = entry.getString("permission");
            double value = entry.getDouble("value", 1.0D);

            if (permission == null || permission.isEmpty()) continue;

            if (player.hasPermission(permission) && value > best) {
                best = value;
            }
        }

        return best;
    }

    public double calculateReward(Player player) {
        double total = getBaseReward() * getBestMultiplier(player);
        return round(total);
    }

    public double round(double value) {
        return new BigDecimal(value).setScale(getRounding(), RoundingMode.HALF_UP).doubleValue();
    }

    public String format(double value) {
        double rounded = round(value);

        if (rounded == (long) rounded) {
            return String.valueOf((long) rounded);
        }

        return String.valueOf(rounded);
    }

    public boolean rewardKill(Player killer) {
        if (!isEnabled() || killer == null) return false;

        double reward = calculateReward(killer);
        if (reward <= 0.0D) return false;

        return plugin.getVaultEconomyHook().deposit(killer, reward);
    }

    public String buildActionBar(Player killer) {
        String template = plugin.getConfig().getString("coins.actionbar", "&6+%coins% coins &7(x%multiplier%)");

        double multiplier = getBestMultiplier(killer);
        double reward = calculateReward(killer);

        return ConfigUtil.color(template
                .replace("%coins%", format(reward))
                .replace("%multiplier%", format(multiplier)));
    }
}