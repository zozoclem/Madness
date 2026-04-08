package fr.europixel.madness.manager;

import fr.europixel.madness.model.LeaderboardEntry;
import fr.europixel.madness.MadnessPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {

    private final MadnessPlugin plugin;
    private BukkitTask task;
    private List<LeaderboardEntry> topKills = new ArrayList<LeaderboardEntry>();
    private List<LeaderboardEntry> topStreaks = new ArrayList<LeaderboardEntry>();

    public LeaderboardManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();

        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    return;
                }

                refresh();
            }
        }, 0L, 20L * 30L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void refresh() {
        if (!plugin.isEnabled() || plugin.getDatabaseManager() == null) {
            return;
        }

        try {
            this.topKills = plugin.getDatabaseManager().getTopKills(10);
            this.topStreaks = plugin.getDatabaseManager().getTopBestStreaks(10);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerKillRank(String playerName) {
        if (playerName == null) {
            return -1;
        }

        for (int i = 0; i < topKills.size(); i++) {
            LeaderboardEntry entry = topKills.get(i);

            if (entry != null && entry.getName() != null && entry.getName().equalsIgnoreCase(playerName)) {
                return i + 1;
            }
        }

        return -1;
    }

    public LeaderboardEntry getTopKillEntry(int rank) {
        if (rank < 1 || rank > topKills.size()) {
            return null;
        }
        return topKills.get(rank - 1);
    }

}