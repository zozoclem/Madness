package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaSidebar extends SidebarContents {

    public ArenaSidebar(MadnessPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public String[] getLines() {
        Player player = getPlayer();
        MadnessPlugin plugin = getPlugin();
        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);

        List<String> configured = plugin.getSidebarConfig().getConfig().getStringList("arena.lines");
        List<String> lines = new ArrayList<String>();

        for (String line : configured) {
            if ("%cooldowns%".equalsIgnoreCase(line)) {
                addCooldownLines(lines, plugin, player);
                continue;
            }

            lines.add(color(replacePlaceholders(line, player, stats, plugin)));
        }

        return lines.toArray(new String[0]);
    }

    private void addCooldownLines(List<String> lines, MadnessPlugin plugin, Player player) {
        boolean tnt = plugin.getRechargeManager().isTntOnCooldown(player);
        boolean jetpack = plugin.getRechargeManager().isJetpackOnCooldown(player);

        if (!tnt && !jetpack) {
            return;
        }

        lines.add(color(plugin.getSidebarConfig().getConfig().getString("arena.cooldown-header", " §8❙ §e§lCooldown")));

        if (tnt) {
            String line = plugin.getSidebarConfig().getConfig().getString("arena.tnt-line", "   &8➥ &7TNT: &f%tnt%s");
            line = line.replace("%tnt%", String.valueOf(plugin.getRechargeManager().getRemainingTntSeconds(player)));
            lines.add(color(line));
        }

        if (jetpack) {
            String line = plugin.getSidebarConfig().getConfig().getString("arena.jetpack-line", "   &8➥ &7Jetpack: &f%jetpack%s");
            line = line.replace("%jetpack%", String.valueOf(plugin.getRechargeManager().getRemainingJetpackSeconds(player)));
            lines.add(color(line));
        }
    }

    private String replacePlaceholders(String line, Player player, PlayerStats stats, MadnessPlugin plugin) {
        return line
                .replace("%player%", player.getName())
                .replace("%ping%", String.valueOf(((CraftPlayer) player).getHandle().ping))
                .replace("%kills%", String.valueOf(stats.getKills()))
                .replace("%deaths%", String.valueOf(stats.getDeaths()))
                .replace("%streak%", String.valueOf(stats.getStreak()))
                .replace("%tnt%", String.valueOf(plugin.getRechargeManager().getRemainingTntSeconds(player)))
                .replace("%jetpack%", String.valueOf(plugin.getRechargeManager().getRemainingJetpackSeconds(player)));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}