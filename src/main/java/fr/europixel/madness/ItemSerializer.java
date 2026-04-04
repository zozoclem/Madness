package fr.europixel.madness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemSerializer {

    public static String toBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            dataOutput.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de serializer la hotbar.", e);
        }
    }

    public static ItemStack[] fromBase64(String data) {
        try {
            byte[] raw = Base64.getDecoder().decode(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(raw));

            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de désérializer la hotbar.", e);
        }
    }
}