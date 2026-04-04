package fr.europixel.madness;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EditKitManager {

    private final Map<UUID, ItemStack[]> hotbars = new HashMap<UUID, ItemStack[]>();
    private final Set<UUID> closingSafely = new HashSet<UUID>();

    public void saveHotbar(UUID uuid, ItemStack[] hotbar) {
        ItemStack[] copy = new ItemStack[9];

        for (int i = 0; i < 9; i++) {
            if (hotbar[i] != null) {
                copy[i] = hotbar[i].clone();
            }
        }

        hotbars.put(uuid, copy);
    }

    public ItemStack[] getHotbar(UUID uuid) {
        ItemStack[] saved = hotbars.get(uuid);
        if (saved == null) {
            return null;
        }

        ItemStack[] copy = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            if (saved[i] != null) {
                copy[i] = saved[i].clone();
            }
        }

        return copy;
    }

    public boolean hasHotbar(UUID uuid) {
        return hotbars.containsKey(uuid);
    }

    public void markClosingSafely(UUID uuid) {
        closingSafely.add(uuid);
    }

    public boolean consumeClosingSafely(UUID uuid) {
        return closingSafely.remove(uuid);
    }
}