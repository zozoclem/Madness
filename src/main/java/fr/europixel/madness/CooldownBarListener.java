package fr.europixel.madness;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class CooldownBarListener implements Listener {

    private final MadnessPlugin plugin;

    public CooldownBarListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                updateBar(player);
            }
        }, 1L);
    }

    public void updateBar(Player player) {
        ItemRechargeManager.RechargeData data = plugin.getRechargeManager().getHeldRecharge(player);

        if (data == null) {
            player.setLevel(0);
            player.setExp(0.0F);
            return;
        }

        player.setLevel(data.getRemainingSeconds());
        player.setExp(data.getProgress());
    }
}