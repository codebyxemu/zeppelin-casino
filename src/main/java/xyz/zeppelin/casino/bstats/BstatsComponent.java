package xyz.zeppelin.casino.bstats;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.component.BasePluginComponent;

/**
 * A plugin component that registers bStats metrics.
 */
public class BstatsComponent extends BasePluginComponent {

    private static final int METRICS_ID = 21073; // TODO: Replace with your own bStats ID
    private Metrics metrics;

    public BstatsComponent(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(plugin, METRICS_ID);
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }
}
