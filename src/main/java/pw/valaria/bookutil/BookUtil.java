package pw.valaria.bookutil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

public class BookUtil {

    private static final String BOOK_CHANNEL = "MC|BOpen";
    private Method addChannelMethod = null;
    private Plugin plugin;

    /**
     * @param plugin plugin to bind to the instance
     */
    public BookUtil(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BOOK_CHANNEL);
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
            player.getInventory().setItemInMainHand(book);
            player.sendPluginMessage(plugin, BOOK_CHANNEL, new byte[]{0});
        }

        player.getInventory().setItemInMainHand(old);
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

