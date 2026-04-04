package fr.europixel.madness.sidebar;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}