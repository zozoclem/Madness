package fr.europixel.madness.chat;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final MadnessPlugin plugin;

    public ChatListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("chat.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();

        String prefix = "";
        if (plugin.getVaultChatHook() != null && plugin.getVaultChatHook().isAvailable()) {
            prefix = plugin.getVaultChatHook().getPrefix(player);
        }

        if (prefix == null) {
            prefix = "";
        }

        String rawFormat = plugin.getConfig().getString(
                "chat.format",
                "%prefix% &7%player% &8» &f%message%"
        );

        String message = event.getMessage();

        String formatted = rawFormat
                .replace("%prefix%", prefix)
                .replace("%player%", player.getName())
                .replace("%message%", message);

        formatted = ConfigUtil.color(formatted).trim();

        event.setCancelled(true);

        final String finalFormatted = formatted;
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(finalFormatted);
            }
        });
    }
}