package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerJoinRespawnListener implements Listener {

    private final MadnessPlugin plugin;

    public PlayerJoinRespawnListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        plugin.getRechargeManager().clear(player);
        plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
        plugin.getLobbyManager().sendToLobby(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        plugin.getRechargeManager().clear(event.getPlayer());
        plugin.getPlayerModeManager().remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setDeathMessage(null);

        final Player player = event.getEntity();

        plugin.getRechargeManager().clear(player);
        plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    player.spigot().respawn();
                }
            }
        }, 2L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        event.setRespawnLocation(plugin.getLobbyManager().getLobbySpawn());

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    plugin.getRechargeManager().clear(player);
                    plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
                    plugin.getLobbyManager().sendToLobby(player);
                }
            }
        }, 1L);
    }
}