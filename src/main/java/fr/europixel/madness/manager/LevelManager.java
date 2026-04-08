package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerStats;
import fr.europixel.madness.bar.ActionBarUtil;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelManager {

    private final MadnessPlugin plugin;

    public LevelManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public int getKillXp() {
        return plugin.getConfig().getInt("levels.kill-xp", 10);
    }

    public int getXpNeeded(int level) {
        int base = plugin.getConfig().getInt("levels.base-xp", 100);
        int perLevel = plugin.getConfig().getInt("levels.per-level", 25);

        if (level < 1) {
            level = 1;
        }

        return base + ((level - 1) * perLevel);
    }

    public void rewardKill(Player player) {
        addXp(player, getKillXp());
    }

    public void addXp(Player player, int amount) {
        if (player == null || amount <= 0) {
            return;
        }

        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
        if (stats == null) {
            return;
        }

        stats.addXp(amount);

        String xpMessage = plugin.getConfig().getString("levels.xp-actionbar", "&b+%xp% xp");
        if (xpMessage != null && !xpMessage.trim().isEmpty()) {
            ActionBarUtil.sendActionBar(player, ConfigUtil.color(
                    xpMessage.replace("%xp%", String.valueOf(amount))
            ));
        }

        boolean leveledUp = false;

        while (stats.getXp() >= getXpNeeded(stats.getLevel())) {
            int needed = getXpNeeded(stats.getLevel());
            stats.setXp(stats.getXp() - needed);
            stats.setLevel(stats.getLevel() + 1);
            leveledUp = true;

            String levelUpMessage = plugin.getConfig().getString("levels.level-up-message", "&aLevel up! &7You are now level &e%level%");
            player.sendMessage(ConfigUtil.color(
                    levelUpMessage.replace("%level%", String.valueOf(stats.getLevel()))
            ));

            try {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            } catch (Exception ignored) {
            }
        }

        if (leveledUp) {
            String levelUpActionBar = plugin.getConfig().getString("levels.level-up-actionbar", "&aLevel &e%level%&a!");
            ActionBarUtil.sendActionBar(player, ConfigUtil.color(
                    levelUpActionBar.replace("%level%", String.valueOf(stats.getLevel()))
            ));
        }

        plugin.getPlayerStatsManager().savePlayer(player);

        if (plugin.getSidebarManager() != null) {
            plugin.getSidebarManager().update(player);
        }
    }
}