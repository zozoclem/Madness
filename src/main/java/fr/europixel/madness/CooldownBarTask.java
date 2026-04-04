package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CooldownBarTask {

    private final MadnessPlugin plugin;
    private final CooldownBarListener listener;
    private BukkitTask task;

    public CooldownBarTask(MadnessPlugin plugin, CooldownBarListener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    public void start() {
        stop();

        this.task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    listener.updateBar(player);
                }
            }
        }, 1L, 2L);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}