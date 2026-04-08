package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatsManager {

    private final MadnessPlugin plugin;
    private final Map<UUID, PlayerStats> cache = new HashMap<UUID, PlayerStats>();
    private final Set<UUID> saveQueue = ConcurrentHashMap.newKeySet();

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

    public void queueSave(Player player) {
        if (player == null) return;

        PlayerStats stats = getStats(player);
        if (stats == null) return;

        stats.markDirty();
        saveQueue.add(player.getUniqueId());
    }

    public void flushQueuedSaves() {
        Iterator<UUID> iterator = saveQueue.iterator();

        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            PlayerStats stats = getStats(player);
            if (stats == null || !stats.isDirty()) continue;

            try {
                savePlayerSync(player);
            } catch (Exception e) {
                e.printStackTrace();
                saveQueue.add(uuid);
            }
        }
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
                    stats.setDirty(false);
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
            stats.setDirty(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unloadPlayer(Player player) {
        PlayerStats stats = cache.get(player.getUniqueId());

        if (stats != null) {
            stats.resetStreak();
            queueSave(player);
            savePlayerSync(player);
        }

        saveQueue.remove(player.getUniqueId());
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
        queueSave(player);
    }

    public void addDeath(Player player) {
        getStats(player).addDeath();
        queueSave(player);
    }

    public void setHotbar(Player player, ItemStack[] hotbar) {
        getStats(player).setHotbar(cloneHotbar(hotbar));
        queueSave(player);
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