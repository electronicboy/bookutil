package pw.valaria.bookutil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.bukkit.plugin.messaging.StandardMessenger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

public class BookUtil {

    private final String BOOK_CHANNEL;
    private static final String BOOK_CHANNEL_PRE13 = "MC|BOpen";
    private static final String BOOK_CHANNEL_13 = "minecraft:book_open";
    private Method addChannelMethod = null;
    private Plugin plugin;
    private boolean useLegacyItemInHand = false;

    /**
     * @param plugin plugin to bind to the instance
     */
    public BookUtil(Plugin plugin) {
        this.plugin = plugin;

        // attempt to workout the version that is in play - maybe some prettier way to do this?
        String bukkitVersion = plugin.getServer().getVersion();
        String[] mcVersion = bukkitVersion.substring(bukkitVersion.lastIndexOf(" ") + 1, bukkitVersion.lastIndexOf(")")).split("\\.");

        int[] versionParts = new int[mcVersion.length];
        for (int i = 0; i < versionParts.length; i++) {
            versionParts[i] = Integer.valueOf(mcVersion[i]);
        }

        if (versionParts.length < 2) {
            throw new IllegalArgumentException("Unable to detect server version!");
        }

        if (versionParts[1] >= 13) {
            BOOK_CHANNEL = BOOK_CHANNEL_13;
        } else {
            BOOK_CHANNEL = BOOK_CHANNEL_PRE13;
        }

        final Messenger messenger = plugin.getServer().getMessenger();

        try {
            // This bypasses 1.13s sanity checks, and allows us to actually register the message
            final Class<? extends Messenger> messengerClass = messenger.getClass();
            final Method addToOutgoing = messengerClass.getDeclaredMethod("addToOutgoing", Plugin.class, String.class);
            addToOutgoing.setAccessible(true);
            addToOutgoing.invoke(messenger, plugin, BOOK_CHANNEL);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    /**
     * @param player the player to open the book to
     * @param book   the book to open
     */
    public void openBook(@Nonnull Player player, @Nonnull ItemStack book) {
        if (book.getType() != Material.WRITTEN_BOOK)
            throw new IllegalArgumentException("Expected a written book!");

        ItemStack old = player.getInventory().getItemInMainHand();

        if (checkChannel(player)) {
            setItemInHand(player, book);
            player.sendPluginMessage(plugin, BOOK_CHANNEL, new byte[]{0});
        }

        setItemInHand(player, old);
    }

    private void setItemInHand(Player player, ItemStack itemStack) {
        if (!useLegacyItemInHand) {
            try {
                player.getInventory().setItemInMainHand(itemStack);
            } catch (NoSuchMethodError ignored) {
                useLegacyItemInHand = true;
            }
        }

        if (useLegacyItemInHand) {
            //noinspection deprecation
            player.getInventory().setItemInHand(itemStack);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean checkChannel(Player player) {

        if (!player.getListeningPluginChannels().contains(BOOK_CHANNEL)) {

            if (addChannelMethod == null) {
                Class<? extends Player> aClass = player.getClass();

                try {
                    addChannelMethod = aClass.getMethod("addChannel", String.class);
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }


            if (addChannelMethod != null) {
                try {
                    addChannelMethod.invoke(player, BOOK_CHANNEL);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
        }

        return player.getListeningPluginChannels().contains(BOOK_CHANNEL);
    }
}

