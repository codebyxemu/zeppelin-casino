package xyz.zeppelin.casino.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public final class TextInputUserInterface implements Listener {

    private final Plugin plugin;
    private final Player player;
    private final Function<String, String> callback;

    private TextInputUserInterface(Plugin plugin, Player player, Function<String, String> callback) {
        this.plugin = plugin;
        this.player = player;
        this.callback = callback;
    }

    private void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        unregister();
    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;
        event.setCancelled(true);
        String errorMessage = callback.apply(event.getMessage());
        if (errorMessage != null) {
            player.sendMessage(errorMessage);
        }
        unregister();
    }

    public static void open(Plugin plugin, Player player, Function<String, String> callback) {
        new TextInputUserInterface(plugin, player, callback).register();
    }
}
