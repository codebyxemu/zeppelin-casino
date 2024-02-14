package xyz.zeppelin.casino.component;

/**
 * Plugin components represent a part of the plugin functionality.
 * Listeners, commands, and other features should be implemented as components.
 * Components are registered and unregistered when the plugin is enabled and disabled.
 */
public interface PluginComponent {

    /**
     * Executed when the plugin is loaded.
     */
    default void onLoad() {
        // Do nothing by default for convenience
    }

    /**
     * Executed when the plugin is enabled.
     */
    default void onEnable() {
        // Do nothing by default for convenience
    }

    /**
     * Executed when the plugin is disabled.
     */
    default void onDisable() {
        // Do nothing by default for convenience
    }
}
