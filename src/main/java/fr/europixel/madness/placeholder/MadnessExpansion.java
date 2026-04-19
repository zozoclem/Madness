package fr.europixel.madness.placeholder;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.LeaderboardEntry;
import fr.europixel.madness.model.PlayerStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.sql.SQLException;

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
            if (rank == -1) return "";

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopKillEntry(rank);
            return entry == null ? "Aucun" : entry.getName();
        }

        if (params.startsWith("top_kills_value_")) {
            int rank = parseRank(params, "top_kills_value_");
            if (rank == -1) return "0";

            LeaderboardEntry entry = plugin.getLeaderboardManager().getTopKillEntry(rank);
            return entry == null ? "0" : String.valueOf(entry.getValue());
        }

        if (params.equalsIgnoreCase("rank_kills")) {
            if (plugin.getDatabaseManager() == null) return "0";

            try {
                int rank = plugin.getDatabaseManager().getPlayerKillRank(player.getName());
                return rank <= 0 ? "0" : String.valueOf(rank);
            } catch (SQLException e) {
                e.printStackTrace();
                return "0";
            }
        }

        if (player == null) return "";

        if (plugin.getPlayerStatsManager() == null) return "0";

        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);
        if (stats == null) return "0";

        if (params.equalsIgnoreCase("kills")) {
            return String.valueOf(stats.getKills());
        }

        if (params.equalsIgnoreCase("deaths")) {
            return String.valueOf(stats.getDeaths());
        }

        if (params.equalsIgnoreCase("streak")) {
            return String.valueOf(stats.getStreak());
        }

        if (params.equalsIgnoreCase("tnt_cooldown")) {
            if (plugin.getRechargeManager() == null) return "0";
            return String.valueOf(plugin.getRechargeManager().getRemainingTntSeconds(player));
        }

        if (params.equalsIgnoreCase("jetpack_cooldown")) {
            if (plugin.getRechargeManager() == null) return "0";
            return String.valueOf(plugin.getRechargeManager().getRemainingJetpackSeconds(player));
        }

        if (params.equalsIgnoreCase("has_cooldowns")) {
            if (plugin.getRechargeManager() == null) return "false";

            double tnt = plugin.getRechargeManager().getRemainingTntSeconds(player);
            double jetpack = plugin.getRechargeManager().getRemainingJetpackSeconds(player);

            return (tnt > 0 || jetpack > 0) ? "true" : "false";
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