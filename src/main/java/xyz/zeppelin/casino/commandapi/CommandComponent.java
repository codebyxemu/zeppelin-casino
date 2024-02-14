package xyz.zeppelin.casino.commandapi;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.component.BasePluginComponent;

/**
 * A plugin component that implements a command using the CommandAPI.
 */
public abstract class CommandComponent extends BasePluginComponent {

    private final CommandAPICommand command;

    public CommandComponent(JavaPlugin plugin) {
        super(plugin);
        this.command = createCommand();
    }

    public abstract CommandAPICommand createCommand();

    @Override
    public void onEnable() {
        command.register();
    }
}
