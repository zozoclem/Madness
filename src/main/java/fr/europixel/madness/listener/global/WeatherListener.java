package fr.europixel.madness.listener.global;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;

public class WeatherListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        if (e.toThunderState()) {
            e.setCancelled(true);
        }
    }

    // Force le jour en permanence
    public static void forceDay() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(1000); // matin
        }
    }
}