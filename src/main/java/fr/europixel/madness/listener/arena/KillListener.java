package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.bar.ActionBarUtil;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillListener implements Listener {

    private final MadnessPlugin plugin;

    public KillListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (plugin.getPlayerModeManager().isInArena(victim)) {
            return;
        }

        Player killer = victim.getKiller();

        plugin.getPlayerStatsManager().addDeath(victim);

        if (killer == null) {
            sendDeathActionBar(victim, null);

            if (plugin.getSidebarManager() != null) {
                plugin.getSidebarManager().update(victim);
            }
            return;
        }

        plugin.getPlayerStatsManager().addKill(killer);

        healInstant(killer);
        giveRewardItem(killer);
        plugin.getRechargeManager().resetTnt(killer);
        plugin.getRechargeManager().resetJetpack(killer);

        rewardCoins(killer);
        rewardXp(killer);

        sendKillActionBar(killer, victim);
        sendDeathActionBar(victim, killer);
        broadcastKillStreak(killer);

        if (plugin.getSidebarManager() != null) {
            plugin.getSidebarManager().update(killer);
            plugin.getSidebarManager().update(victim);
        }
    }

    private void healInstant(Player killer) {
        double heal = plugin.getConfig().getDouble("kill-rewards.heal", 8.0D);
        double maxHealth = plugin.getConfig().getDouble("kill-rewards.max-health", 20.0D);

        double newHealth = killer.getHealth() + heal;
        if (newHealth > maxHealth) {
            newHealth = maxHealth;
        }
        if (newHealth > killer.getMaxHealth()) {
            newHealth = killer.getMaxHealth();
        }

        killer.setHealth(newHealth);

        if (plugin.getConfig().getBoolean("kill-rewards.clear-fire", true)) {
            killer.setFireTicks(0);
        }
    }

    private void giveRewardItem(Player killer) {
        String materialName = plugin.getConfig().getString("kill-rewards.item.material", "GOLDEN_APPLE");
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            material = Material.GOLDEN_APPLE;
        }

        int amount = plugin.getConfig().getInt("kill-rewards.item.amount", 1);
        killer.getInventory().addItem(new ItemStack(material, amount));
        killer.updateInventory();
    }

    private void sendKillActionBar(Player killer, Player victim) {
        String killTemplate = plugin.getConfig().getString(
                "kill-rewards.actionbar",
                "&aYou killed &f%victim%"
        );

        StringBuilder message = new StringBuilder();
        message.append(ConfigUtil.color(killTemplate.replace("%victim%", victim.getName())));

        if (plugin.getCoinRewardManager() != null && plugin.getCoinRewardManager().isEnabled()) {
            String coinMessage = plugin.getCoinRewardManager().buildActionBar(killer);
            if (coinMessage != null && !coinMessage.trim().isEmpty()) {
                message.append(ConfigUtil.color(" &8| "));
                message.append(coinMessage);
            }
        }

        if (plugin.getLevelManager() != null) {
            int xp = plugin.getLevelManager().getKillXp();
            if (xp > 0) {
                String xpTemplate = plugin.getConfig().getString("levels.xp-actionbar", "&b+%xp% xp");
                message.append(ConfigUtil.color(" &8| "));
                message.append(ConfigUtil.color(
                        xpTemplate.replace("%xp%", String.valueOf(xp))
                ));
            }
        }

        ActionBarUtil.sendActionBar(killer, message.toString());
    }

    private void sendDeathActionBar(Player victim, Player killer) {
        String template;

        if (killer == null) {
            template = plugin.getConfig().getString("kill-rewards.death-actionbar-no-killer", "&cYou died");
            ActionBarUtil.sendActionBar(victim, ConfigUtil.color(template));
            return;
        }

        template = plugin.getConfig().getString("kill-rewards.death-actionbar", "&cKilled by &f%killer%");
        ActionBarUtil.sendActionBar(victim, ConfigUtil.color(template.replace("%killer%", killer.getName())));
    }

    private void rewardCoins(Player killer) {
        if (plugin.getCoinRewardManager() == null) {
            return;
        }

        plugin.getCoinRewardManager().rewardKill(killer);
    }

    private void rewardXp(Player killer) {
        if (plugin.getLevelManager() == null) {
            return;
        }

        plugin.getLevelManager().rewardKill(killer, false);
    }

    private void broadcastKillStreak(Player killer) {
        int streak = plugin.getPlayerStatsManager().getStats(killer).getStreak();
        if (streak <= 0 || streak % 5 != 0) {
            return;
        }

        String adjective = getStreakAdjective(streak);
        String template = plugin.getConfig().getString(
                "killstreak.broadcast-message",
                "&6%player% &fis &c%adjective%&f! &7(%streak% kill streak)"
        );

        String message = ConfigUtil.color(template
                .replace("%player%", killer.getName())
                .replace("%streak%", String.valueOf(streak))
                .replace("%adjective%", adjective));

        Bukkit.broadcastMessage(message);
    }

    private String getStreakAdjective(int streak) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("killstreak.messages");
        if (section != null) {
            String configured = section.getString(String.valueOf(streak));
            if (configured != null && !configured.trim().isEmpty()) {
                return configured;
            }
        }

        if (streak >= 30) {
            return "unstoppable";
        }
        if (streak >= 25) {
            return "godlike";
        }
        if (streak >= 20) {
            return "legendary";
        }
        if (streak >= 15) {
            return "insane";
        }
        if (streak >= 10) {
            return "monstrous";
        }

        return "incredible";
    }
}