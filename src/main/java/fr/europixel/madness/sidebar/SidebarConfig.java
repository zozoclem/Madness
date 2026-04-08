package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SidebarConfig {

    private final MadnessPlugin plugin;
    private File file;
    private FileConfiguration config;

    public SidebarConfig(MadnessPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "sidebar.yml");

        if (!file.exists()) {
            plugin.saveResource("sidebar.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getUpdateTicks() {
        return config.getInt("update-ticks", 20);
    }

    public String getTitle() {
        return config.getString("title", "&6&lMADNESS");
    }

    public String getTitle(String section) {
        return config.getString(section + ".title", getTitle());
    }

    public List<String> getLines(String section) {
        List<String> lines = config.getStringList(section + ".lines");
        return lines == null ? new ArrayList<String>() : lines;
    }

    public String getRawString(String path, String def) {
        return config.getString(path, def);
    }
}