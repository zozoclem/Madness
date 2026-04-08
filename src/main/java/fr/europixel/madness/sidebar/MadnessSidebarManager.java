package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class MadnessSidebarManager {

    private final MadnessPlugin plugin;
    private BukkitTask task;

    private static final String[] ENTRIES = {
            ChatColor.BLACK.toString(),
            ChatColor.DARK_BLUE.toString(),
            ChatColor.DARK_GREEN.toString(),
            ChatColor.DARK_AQUA.toString(),
            ChatColor.DARK_RED.toString(),
            ChatColor.DARK_PURPLE.toString(),
            ChatColor.GOLD.toString(),
            ChatColor.GRAY.toString(),
            ChatColor.DARK_GRAY.toString(),
            ChatColor.BLUE.toString(),
            ChatColor.GREEN.toString(),
            ChatColor.AQUA.toString(),
            ChatColor.RED.toString(),
            ChatColor.LIGHT_PURPLE.toString(),
            ChatColor.YELLOW.toString()
    };

    public MadnessSidebarManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        start();
    }

    public void start() {
        stop();

        task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!plugin.getSidebarConfig().isEnabled()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    update(player);
                }
            }
        }, 0L, plugin.getSidebarConfig().getUpdateTicks());
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reload() {
        plugin.getSidebarConfig().reload();
        start();
    }

    public void update(Player player) {
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        String section = getSection(player);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String title = apply(plugin.getSidebarConfig().getTitle(section), player);
        objective.setDisplayName(cut(title, 32));

        List<String> lines = buildLines(player, section);
        int maxLines = Math.min(lines.size(), 15);
        int score = maxLines;

        for (int i = 0; i < maxLines; i++) {
            String line = lines.get(i);
            String entry = ENTRIES[i];

            Team team = scoreboard.registerNewTeam("line_" + i);

            String prefix;
            String suffix;

            if (line.length() <= 16) {
                prefix = line;
                suffix = "";
            } else {
                prefix = line.substring(0, 16);

                if (prefix.endsWith("§")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }

                String rest = line.substring(prefix.length());
                String lastColors = ChatColor.getLastColors(prefix);

                suffix = lastColors + rest;
                suffix = cut(suffix, 16);

                if (suffix.endsWith("§")) {
                    suffix = suffix.substring(0, suffix.length() - 1);
                }
            }

            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.addEntry(entry);

            objective.getScore(entry).setScore(score);
            score--;
        }

        player.setScoreboard(scoreboard);
    }

    private List<String> buildLines(Player player, String section) {
        List<String> result = new ArrayList<String>();
        List<String> configuredLines = plugin.getSidebarConfig().getLines(section);

        for (String rawLine : configuredLines) {
            if (rawLine == null) {
                continue;
            }

            if (rawLine.contains("%cooldowns%")) {
                List<String> cooldownLines = buildCooldownLines(player, section);

                if (!cooldownLines.isEmpty()) {
                    result.addAll(cooldownLines);
                }
                continue;
            }

            String parsed = apply(rawLine, player);

            if (parsed == null) {
                continue;
            }

            result.add(parsed);
        }

        return result;
    }

    private List<String> buildCooldownLines(Player player, String section) {
        List<String> lines = new ArrayList<>();

        int tnt = plugin.getRechargeManager() == null ? 0 : plugin.getRechargeManager().getRemainingTntSeconds(player);
        int jetpack = plugin.getRechargeManager() == null ? 0 : plugin.getRechargeManager().getRemainingJetpackSeconds(player);

        // ❌ Aucun cooldown → RIEN DU TOUT
        if (tnt <= 0 && jetpack <= 0) {
            return lines;
        }

        String header = plugin.getSidebarConfig().getRawString(section + ".cooldown-header", "");
        String tntLine = plugin.getSidebarConfig().getRawString(section + ".tnt-line", "");
        String jetpackLine = plugin.getSidebarConfig().getRawString(section + ".jetpack-line", "");

        // ✅ ESPACE AVANT (UNIQUEMENT SI cooldown actif)
        lines.add(apply("&7 ", player));

        // Header
        if (header != null && !header.isEmpty()) {
            lines.add(apply(header, player));
        }

        // TNT
        if (tnt > 0 && tntLine != null && !tntLine.isEmpty()) {
            lines.add(apply(tntLine, player));
        }

        // Jetpack
        if (jetpack > 0 && jetpackLine != null && !jetpackLine.isEmpty()) {
            lines.add(apply(jetpackLine, player));
        }

        return lines;
    }

    private String getSection(Player player) {
        if (plugin.getPlayerModeManager() != null && plugin.getPlayerModeManager().isInArena(player)) {
            return "arena";
        }
        return "lobby";
    }

    private String apply(String text, Player player) {
        if (text == null) {
            return "";
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    private String cut(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}