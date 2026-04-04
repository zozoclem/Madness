package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbySidebar extends SidebarContents {

    public LobbySidebar(MadnessPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public String[] getLines() {
        Player player = getPlayer();
        MadnessPlugin plugin = getPlugin();
        PlayerStats stats = plugin.getPlayerStatsManager().getStats(player);

        List<String> configured = plugin.getSidebarConfig().getConfig().getStringList("lobby.lines");
        List<String> lines = new ArrayList<String>();

        for (String line : configured) {
            lines.add(color(replacePlaceholders(line, player, stats)));
        }

        return lines.toArray(new String[0]);
    }

    private String replacePlaceholders(String line, Player player, PlayerStats stats) {
        return line
                .replace("%player%", player.getName())
                .replace("%ping%", String.valueOf(((CraftPlayer) player).getHandle().ping))
                .replace("%kills%", String.valueOf(stats.getKills()))
                .replace("%deaths%", String.valueOf(stats.getDeaths()))
                .replace("%streak%", String.valueOf(stats.getStreak()));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}