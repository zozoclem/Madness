package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EventHistoryManager {

    private final MadnessPlugin plugin;

    public EventHistoryManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void logKill(final Player killer, final Player victim, final String cause) {
        if (killer == null || victim == null) {
            return;
        }

        final double coins = plugin.getCoinRewardManager() != null
                ? plugin.getCoinRewardManager().calculateReward(killer)
                : 0.0D;

        final int xp = plugin.getLevelManager() != null
                ? plugin.getLevelManager().getKillXp()
                : 0;

        int streak = 0;
        if (plugin.getPlayerStatsManager() != null) {
            PlayerStats stats = plugin.getPlayerStatsManager().getStats(killer);
            if (stats != null) {
                streak = stats.getStreak();
            }
        }

        final int finalStreak = streak;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.getDatabaseManager().insertEvent(
                            "KILL",
                            killer.getUniqueId().toString(),
                            killer.getName(),
                            victim.getUniqueId().toString(),
                            victim.getName(),
                            killer.getWorld().getName(),
                            cause,
                            coins,
                            xp,
                            finalStreak,
                            "killer=" + killer.getName() + ",victim=" + victim.getName()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logDeath(final Player victim, final String cause) {
        if (victim == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.getDatabaseManager().insertEvent(
                            "DEATH",
                            victim.getUniqueId().toString(),
                            victim.getName(),
                            null,
                            null,
                            victim.getWorld().getName(),
                            cause,
                            0.0D,
                            0,
                            0,
                            "victim=" + victim.getName()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logVoidDeath(final Player victim) {
        if (victim == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.getDatabaseManager().insertEvent(
                            "VOID_DEATH",
                            victim.getUniqueId().toString(),
                            victim.getName(),
                            null,
                            null,
                            victim.getWorld().getName(),
                            "VOID",
                            0.0D,
                            0,
                            0,
                            "victim=" + victim.getName()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logArenaBoundaryDeath(final Player victim) {
        if (victim == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.getDatabaseManager().insertEvent(
                            "ARENA_BOUNDARY_DEATH",
                            victim.getUniqueId().toString(),
                            victim.getName(),
                            null,
                            null,
                            victim.getWorld().getName(),
                            "ARENA_BOUNDARY",
                            0.0D,
                            0,
                            0,
                            "victim=" + victim.getName()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}