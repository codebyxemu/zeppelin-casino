package xyz.zeppelin.casino.component;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.common.Environment;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

class ComponentManagerImpl implements ComponentManager {

    private final Logger logger;
    @Getter
    private final List<PluginComponent> components;

    ComponentManagerImpl(Plugin plugin, List<PluginComponent> pluginComponents) {
        this.logger = plugin.getLogger();
        this.components = pluginComponents;
    }

    @Override
    public void loadComponents() {
        if (Environment.isDevelopmentMode()) {
            logger.info("Loading " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                logger.info("Loading component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onLoad();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                logger.info("Loaded component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            logger.info("Loaded " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onLoad();
            }
        }
    }

    @Override
    public void enableComponents() {
        if (Environment.isDevelopmentMode()) {
            logger.info("Enabling " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                logger.info("Enabling component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onEnable();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                logger.info("Enabled component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            logger.info("Enabled " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onEnable();
            }
        }
    }

    @Override
    public void disableComponents() {
        if (Environment.isDevelopmentMode()) {
            logger.info("Disabling " + components.size() + " components...");
            Instant allStartInstant = Instant.now();
            for (PluginComponent component : components) {
                logger.info("Disabling component " + component.getClass().getName() + "...");
                Instant startInstant = Instant.now();
                component.onDisable();
                Instant endInstant = Instant.now();
                long durationInMillis = endInstant.toEpochMilli() - startInstant.toEpochMilli();
                logger.info("Disabled component " + component.getClass().getName() + " in " + durationInMillis + "ms.");
            }
            Instant allEndInstant = Instant.now();
            long durationInMillis = allEndInstant.toEpochMilli() - allStartInstant.toEpochMilli();
            logger.info("Disabled " + components.size() + " components in " + durationInMillis + "ms.");
        } else {
            for (PluginComponent component : components) {
                component.onDisable();
            }
        }
    }

    @Override
    public <T extends PluginComponent> T getComponent(Class<T> componentClass) {
        for (PluginComponent component : components) {
            if (componentClass.isInstance(component)) return componentClass.cast(component);
        }
        throw new IllegalArgumentException("Component " + componentClass.getName() + " not provided.");
    }
}
