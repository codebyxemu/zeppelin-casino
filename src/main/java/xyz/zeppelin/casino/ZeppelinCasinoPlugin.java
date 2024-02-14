package xyz.zeppelin.casino;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.bstats.BstatsComponent;
import xyz.zeppelin.casino.commandapi.CommandApiComponent;
import xyz.zeppelin.casino.common.Environment;
import xyz.zeppelin.casino.component.PluginComponent;

import java.time.Instant;
import java.util.List;

public class ZeppelinCasinoPlugin extends JavaPlugin {

    /**
     * Components of this plugin that represent different features.
     *
     * @see PluginComponent
     */
    private final List<PluginComponent> components = List.of(
            new CommandApiComponent(this),
            new BstatsComponent(this)
    );

    @Override
    public void onLoad() {
        if (Environment.isDevelopmentMode()) {
            getLogger().warning("Development mode is enabled. Be careful this may be dangerous for production environments!");
        }
        loadComponents();
    }

    private void loadComponents() {
        if (Environment.isDevelopmentMode()) {
            getLogger().info("Loading " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                getLogger().info("Loading component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onLoad();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                getLogger().info("Loaded component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            getLogger().info("Loaded " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onLoad();
            }
        }
    }

    @Override
    public void onEnable() {
        enableComponents();
    }

    private void enableComponents() {
        if (Environment.isDevelopmentMode()) {
            getLogger().info("Enabling " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                getLogger().info("Enabling component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onEnable();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                getLogger().info("Enabled component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            getLogger().info("Enabled " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onEnable();
            }
        }
    }

    @Override
    public void onDisable() {
        disableComponents();
    }

    private void disableComponents() {
        if (Environment.isDevelopmentMode()) {
            getLogger().info("Disabling " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                getLogger().info("Disabling component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onDisable();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                getLogger().info("Disabled component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            getLogger().info("Disabled " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onDisable();
            }
        }
    }
}
