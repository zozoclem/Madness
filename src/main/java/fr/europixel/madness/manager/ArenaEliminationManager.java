package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.bar.ActionBarUtil;
import fr.europixel.madness.model.PlayerMode;
import fr.europixel.madness.model.PlayerStats;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ArenaEliminationManager {

    private final MadnessPlugin plugin;
    private final Set<UUID> processing = new HashSet<UUID>();

    public ArenaEliminationManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isProcessing(Player player) {
        return player != null && processing.contains(player.getUniqueId());
    }

    public void eliminate(final Player victim, final Player killer) {
        if (victim == null || !victim.isOnline()) {
            return;
        }

        if (processing.contains(victim.getUniqueId())) {
            return;
        }

        processing.add(victim.getUniqueId());

        try {
            handleElimination(victim, killer);
        } finally {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    processing.remove(victim.getUniqueId());
                }
            }, 5L);
        }
    }

    private void handleElimination(Player victim, Player killer) {
        plugin.getPlayerStatsManager().addDeath(victim);

        if (killer != null && killer.isOnline() && !killer.getUniqueId().equals(victim.getUniqueId())) {
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
            logKillEvent(killer, victim);

            if (plugin.getSidebarManager() != null) {
                plugin.getSidebarManager().update(killer);
            }
        } else {
            sendDeathActionBar(victim, null);
            logDeathEvent(victim, "NO_KILLER");
        }

        if (plugin.getLastDamagerManager() != null) {
            plugin.getLastDamagerManager().clear(victim);
        }

        plugin.getRechargeManager().clear(victim);
        plugin.getPlayerModeManager().setMode(victim, PlayerMode.LOBBY);

        victim.closeInventory();
        victim.setFireTicks(0);
        victim.setFallDistance(0.0F);
        victim.setNoDamageTicks(20);
        victim.setVelocity(new Vector(0, 0, 0));
        victim.setFoodLevel(plugin.getConfig().getInt("player-reset.food", 20));

        double health = plugin.getConfig().getDouble("player-reset.health", 20.0D);
        if (health > victim.getMaxHealth()) {
            health = victim.getMaxHealth();
        }
        if (health < 1.0D) {
            health = 1.0D;
        }
        victim.setHealth(health);

        plugin.getLobbyManager().sendToLobby(victim);

        if (plugin.getSidebarManager() != null) {
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
        if (killer == null) {
            String template = plugin.getConfig().getString(
                    "kill-rewards.death-actionbar-no-killer",
                    "&cYou died"
            );
            ActionBarUtil.sendActionBar(victim, ConfigUtil.color(template));
            return;
        }

        String template = plugin.getConfig().getString(
                "kill-rewards.death-actionbar",
                "&cKilled by &f%killer%"
        );
        ActionBarUtil.sendActionBar(victim, ConfigUtil.color(
                template.replace("%killer%", killer.getName())
        ));
    }

    private void rewardCoins(Player killer) {
        if (plugin.getCoinRewardManager() != null) {
            plugin.getCoinRewardManager().rewardKill(killer);
        }
    }

    private void rewardXp(Player killer) {
        if (plugin.getLevelManager() != null) {
            plugin.getLevelManager().rewardKill(killer, false);
        }
    }

    private void logKillEvent(Player killer, Player victim) {
        try {
            double coins = 0.0D;
            if (plugin.getCoinRewardManager() != null) {
                coins = plugin.getCoinRewardManager().calculateReward(killer);
            }

            int xp = 0;
            if (plugin.getLevelManager() != null) {
                xp = plugin.getLevelManager().getKillXp();
            }

            int streak = 0;
            PlayerStats stats = plugin.getPlayerStatsManager().getStats(killer);
            if (stats != null) {
                streak = stats.getStreak();
            }

            plugin.getDatabaseManager().insertEvent(
                    "KILL",
                    killer.getUniqueId().toString(),
                    killer.getName(),
                    victim.getUniqueId().toString(),
                    victim.getName(),
                    killer.getWorld().getName(),
                    "PLAYER_KILL",
                    coins,
                    xp,
                    streak,
                    "killer=" + killer.getName() + ",victim=" + victim.getName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logDeathEvent(Player victim, String cause) {
        try {
            plugin.getDatabaseManager().insertEvent(
                    "DEATH",
                    victim.getUniqueId().toString(),
                    victim.getName(),
                    null,
                    null,
                    victim.getWorld().getName(),
                    cause,
                    0.0D,
                    0,
                    0,
                    "victim=" + victim.getName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastKillStreak(Player killer) {
        PlayerStats stats = plugin.getPlayerStatsManager().getStats(killer);
        if (stats == null) {
            return;
        }

        int streak = stats.getStreak();
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