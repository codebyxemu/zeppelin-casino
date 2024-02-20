package xyz.zeppelin.casino.component;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.List;

public interface ComponentManager {

    void loadComponents();

    void enableComponents();

    void disableComponents();

    List<PluginComponent> getComponents();

    <T extends PluginComponent> T getComponent(Class<T> componentClass);

    static ComponentManager register(Plugin plugin, List<PluginComponent> components) {
        ComponentManager instance = new ComponentManagerImpl(plugin, components);
        ServicesManager servicesManager = plugin.getServer().getServicesManager();
        servicesManager.register(ComponentManager.class, instance, plugin, ServicePriority.Highest);
        return instance;
    }

    static ComponentManager getComponentManager(Plugin plugin) {
        return plugin.getServer().getServicesManager().load(ComponentManager.class);
    }
}
