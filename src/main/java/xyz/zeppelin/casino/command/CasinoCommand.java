package xyz.zeppelin.casino.command;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.commandapi.CommandComponent;
import xyz.zeppelin.casino.ui.CasinoUserInterface;

public class CasinoCommand extends CommandComponent {

    public CasinoCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandAPICommand createCommand() {
        return new CommandAPICommand("casino")
                .executesPlayer((sender, args) -> {
                    CasinoUserInterface.open(plugin, sender);
                });
    }
}
