package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MadnessSidebarManager {

    private final MadnessPlugin plugin;
    private final Map<UUID, Sidebar> sidebars = new HashMap<>();

    public MadnessSidebarManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void create(Player player) {
        remove(player);
        sidebars.put(player.getUniqueId(), new Sidebar(plugin, player));
    }

    public void remove(Player player) {
        Sidebar sidebar = sidebars.remove(player.getUniqueId());
        if (sidebar != null) {
            sidebar.delete();
        }
    }

    public void update(Player player) {
        Sidebar sidebar = sidebars.get(player.getUniqueId());
        if (sidebar != null) {
            sidebar.update();
        }
    }
}