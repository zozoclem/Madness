package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsManager {

    private final MadnessPlugin plugin;
    private final Map<UUID, PlayerStats> cache = new HashMap<UUID, PlayerStats>();

    public PlayerStatsManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    final PlayerStats stats = plugin.getDatabaseManager().loadStats(
                            player.getUniqueId().toString(),
                            player.getName()
                    );

                    cache.put(player.getUniqueId(), stats);

                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (player.isOnline() && plugin.getSidebarManager() != null) {
                                plugin.getSidebarManager().update(player);
                            }
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void savePlayer(final Player player) {
        final PlayerStats stats = cache.get(player.getUniqueId());
        if (stats == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.getDatabaseManager().saveStats(
                            player.getUniqueId().toString(),
                            player.getName(),
                            stats
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void savePlayerSync(Player player) {
        PlayerStats stats = cache.get(player.getUniqueId());
        if (stats == null) {
            return;
        }

        try {
            plugin.getDatabaseManager().saveStats(
                    player.getUniqueId().toString(),
                    player.getName(),
                    stats
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unloadPlayer(Player player) {
        PlayerStats stats = cache.get(player.getUniqueId());

        if (stats != null) {
            stats.resetStreak();
        }

        savePlayer(player);
        cache.remove(player.getUniqueId());
    }

    public PlayerStats getStats(Player player) {
        PlayerStats stats = cache.get(player.getUniqueId());
        if (stats == null) {
            stats = new PlayerStats(0, 0, 0, 0, null);
            cache.put(player.getUniqueId(), stats);
        }
        return stats;
    }

    public void addKill(Player player) {
        getStats(player).addKill();
    }

    public void addDeath(Player player) {
        getStats(player).addDeath();
    }

    public void setHotbar(Player player, ItemStack[] hotbar) {
        getStats(player).setHotbar(cloneHotbar(hotbar));
    }

    public ItemStack[] getHotbar(Player player) {
        PlayerStats stats = getStats(player);
        if (stats.getHotbar() == null) {
            return null;
        }
        return cloneHotbar(stats.getHotbar());
    }

    private ItemStack[] cloneHotbar(ItemStack[] hotbar) {
        if (hotbar == null) {
            return null;
        }

        ItemStack[] copy = new ItemStack[hotbar.length];
        for (int i = 0; i < hotbar.length; i++) {
            if (hotbar[i] != null) {
                copy[i] = hotbar[i].clone();
            }
        }
        return copy;
    }
}