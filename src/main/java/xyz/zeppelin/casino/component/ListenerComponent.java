package xyz.zeppelin.casino.component;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin component that implements the bukkit Listener interface.
 *
 * @see Listener
 * @see PluginComponent
 */
public class ListenerComponent extends BasePluginComponent implements Listener {

    /**
     * Create a new listener component.
     *
     * @param plugin the plugin to
     */
    public ListenerComponent(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        pluginManager.registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
