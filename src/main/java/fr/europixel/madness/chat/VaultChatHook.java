package fr.europixel.madness.chat;

import fr.europixel.madness.MadnessPlugin;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultChatHook {

    private final MadnessPlugin plugin;
    private Chat chat;

    public VaultChatHook(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault n'est pas installe. Le systeme de fr.europixel.madness.chat prefixe est desactive.");
            this.chat = null;
            return;
        }

        RegisteredServiceProvider<Chat> provider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (provider == null) {
            plugin.getLogger().warning("Aucun provider de fr.europixel.madness.chat compatible Vault n'a ete trouve. Le systeme de fr.europixel.madness.chat prefixe est desactive.");
            this.chat = null;
            return;
        }

        this.chat = provider.getProvider();

        if (this.chat == null) {
            plugin.getLogger().warning("Le provider de fr.europixel.madness.chat Vault est null. Le systeme de fr.europixel.madness.chat prefixe est desactive.");
            return;
        }

        plugin.getLogger().info("Hook Vault Chat actif avec le provider: " + this.chat.getName());
    }

    public boolean isAvailable() {
        return this.chat != null;
    }

    public String getPrefix(Player player) {
        if (!isAvailable() || player == null) {
            return "";
        }

        String prefix = this.chat.getPlayerPrefix(player);
        return prefix == null ? "" : prefix;
    }
}