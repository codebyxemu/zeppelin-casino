package xyz.zeppelin.casino.commandapi;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.component.BasePluginComponent;

public class CommandApiComponent extends BasePluginComponent {

    public CommandApiComponent(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
