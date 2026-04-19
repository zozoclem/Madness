package fr.europixel.madness.listener.lobby;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.model.PlayerMode;
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

        if (plugin.getLastDamagerManager() != null) {
            plugin.getLastDamagerManager().clear(player);
        }

        if (plugin.getRechargeManager() != null) {
            plugin.getRechargeManager().clear(player);
        }

        if (plugin.getPlayerModeManager() != null) {
            plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
        }

        if (plugin.getLobbyManager() != null) {
            plugin.getLobbyManager().sendToLobby(player);
        }

        if (plugin.getPlayerStatsManager() != null) {
            plugin.getPlayerStatsManager().loadPlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        if (plugin.getLastDamagerManager() != null) {
            plugin.getLastDamagerManager().clear(player);
        }

        if (plugin.getRechargeManager() != null) {
            plugin.getRechargeManager().clear(player);
        }

        if (plugin.getPlayerModeManager() != null) {
            plugin.getPlayerModeManager().remove(player);
        }

        if (plugin.getPlayerStatsManager() != null) {
            plugin.getPlayerStatsManager().unloadPlayer(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setDeathMessage(null);

        final Player player = event.getEntity();

        if (plugin.getLastDamagerManager() != null) {
            plugin.getLastDamagerManager().clear(player);
        }

        if (plugin.getRechargeManager() != null) {
            plugin.getRechargeManager().clear(player);
        }

        if (plugin.getPlayerModeManager() != null) {
            plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
        }

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

        if (plugin.getLastDamagerManager() != null) {
            plugin.getLastDamagerManager().clear(player);
        }

        event.setRespawnLocation(plugin.getLobbyManager().getLobbySpawn());

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    if (plugin.getLastDamagerManager() != null) {
                        plugin.getLastDamagerManager().clear(player);
                    }

                    if (plugin.getRechargeManager() != null) {
                        plugin.getRechargeManager().clear(player);
                    }

                    if (plugin.getPlayerModeManager() != null) {
                        plugin.getPlayerModeManager().setMode(player, PlayerMode.LOBBY);
                    }

                    if (plugin.getLobbyManager() != null) {
                        plugin.getLobbyManager().sendToLobby(player);
                    }
                }
            }
        }, 1L);
    }
}