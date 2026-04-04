package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.PlayerMode;
import fr.europixel.madness.utils.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Sidebar {

    private final MadnessPlugin plugin;
    private final Player player;
    private FastBoard fastBoard;
    private BukkitTask task;

    public Sidebar(MadnessPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.fastBoard = new FastBoard(player);

        start();
    }

    private void start() {
        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::update, 1L, 10L);
    }

    public void update() {
        if (player == null || !player.isOnline()) {
            delete();
            return;
        }

        if (fastBoard.isDeleted()) {
            fastBoard = new FastBoard(player);
        }

        fastBoard.updateTitle(color(plugin.getConfig().getString("sidebar.title", "&6&lMADNESS")));
        SidebarContents contents;

        PlayerMode mode = plugin.getPlayerModeManager().getMode(player);
        if (mode == PlayerMode.ARENA) {
            contents = new ArenaSidebar(plugin, player);
        } else {
            contents = new LobbySidebar(plugin, player);
        }

        fastBoard.updateLines(contents.getLines());
    }

    public void delete() {
        if (task != null) {
            task.cancel();
        }

        if (fastBoard != null && !fastBoard.isDeleted()) {
            fastBoard.delete();
        }
    }

    private String color(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}