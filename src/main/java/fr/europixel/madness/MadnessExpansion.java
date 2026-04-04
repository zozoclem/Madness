package fr.europixel.madness;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MadnessExpansion extends PlaceholderExpansion {

    private final MadnessPlugin plugin;

    public MadnessExpansion(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "madness";
    }

    @Override
    public String getAuthor() {
        return "Europixel";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("top_kills_name_")) {
            int rank = parseRank(params, "top_kills_name_");
            if (rank == -1) {
                return "";
            }

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopKillEntry(rank);
            return entry == null ? "Aucun" : entry.getName();
        }

        if (params.startsWith("top_kills_value_")) {
            int rank = parseRank(params, "top_kills_value_");
            if (rank == -1) {
                return "0";
            }

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopKillEntry(rank);
            return entry == null ? "0" : String.valueOf(entry.getValue());
        }

        if (params.startsWith("top_streak_name_")) {
            int rank = parseRank(params, "top_streak_name_");
            if (rank == -1) {
                return "";
            }

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopStreakEntry(rank);
            return entry == null ? "Aucun" : entry.getName();
        }

        if (params.startsWith("top_streak_value_")) {
            int rank = parseRank(params, "top_streak_value_");
            if (rank == -1) {
                return "0";
            }

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopStreakEntry(rank);
            return entry == null ? "0" : String.valueOf(entry.getValue());
        }

        return null;
    }

    private int parseRank(String params, String prefix) {
        try {
            return Integer.parseInt(params.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}