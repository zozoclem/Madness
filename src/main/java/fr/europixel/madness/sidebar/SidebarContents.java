package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.entity.Player;

public abstract class SidebarContents {

    private final MadnessPlugin plugin;
    private final Player player;

    public SidebarContents(MadnessPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public MadnessPlugin getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract String[] getLines();
}