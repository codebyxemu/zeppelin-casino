package xyz.zeppelin.casino.component;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

/**
 * Base class for plugin components.
 * Provides access to the necessities of a plugin component.
 * Should be used as a base class for all plugin components.
 *
 * @see PluginComponent
 */
public abstract class BasePluginComponent implements PluginComponent {

    protected final JavaPlugin plugin;
    protected final PluginManager pluginManager;
    protected final Server server;
    protected final BukkitScheduler scheduler;
    protected final Logger logger;

    public BasePluginComponent(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.scheduler = plugin.getServer().getScheduler();
        this.pluginManager = plugin.getServer().getPluginManager();
        this.logger = plugin.getLogger();
    }
}
