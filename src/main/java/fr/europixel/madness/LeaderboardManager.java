package fr.europixel.madness;

import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {

    private final MadnessPlugin plugin;

    private List<LeaderboardEntry> topKills = new ArrayList<LeaderboardEntry>();
    private List<LeaderboardEntry> topStreaks = new ArrayList<LeaderboardEntry>();

    public LeaderboardManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 20L, 20L * 30L);
    }

    public void refresh() {
        try {
            topKills = plugin.getDatabaseManager().getTopKills(10);
            topStreaks = plugin.getDatabaseManager().getTopBestStreaks(10);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LeaderboardEntry getTopKillEntry(int rank) {
        if (rank < 1 || rank > topKills.size()) {
            return null;
        }
        return topKills.get(rank - 1);
    }

    public LeaderboardEntry getTopStreakEntry(int rank) {
        if (rank < 1 || rank > topStreaks.size()) {
            return null;
        }
        return topStreaks.get(rank - 1);
    }
}