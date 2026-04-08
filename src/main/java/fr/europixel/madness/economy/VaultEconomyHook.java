package fr.europixel.madness.economy;

import fr.europixel.madness.MadnessPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyHook {

    private final MadnessPlugin plugin;
    private Economy economy;

    public VaultEconomyHook(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault n'est pas installe. Le systeme de coins est desactive.");
            this.economy = null;
            return;
        }

        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            plugin.getLogger().warning("Aucun provider d'economie compatible Vault n'a ete trouve. Le systeme de coins est desactive.");
            this.economy = null;
            return;
        }

        this.economy = provider.getProvider();

        if (this.economy == null) {
            plugin.getLogger().warning("Le provider d'economie Vault est null. Le systeme de coins est desactive.");
            return;
        }

        plugin.getLogger().info("Hook Vault actif avec le provider: " + this.economy.getName());
    }

    public boolean isAvailable() {
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean deposit(Player player, double amount) {
        if (!isAvailable() || player == null || amount <= 0.0D) {
            return false;
        }

        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public boolean withdraw(Player player, double amount) {
        if (!isAvailable() || player == null || amount <= 0.0D) {
            return false;
        }

        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(Player player) {
        if (!isAvailable() || player == null) {
            return 0.0D;
        }

        return economy.getBalance(player);
    }

    public boolean has(Player player, double amount) {
        if (!isAvailable() || player == null) {
            return false;
        }

        return economy.has(player, amount);
    }
}