package xyz.zeppelin.casino.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.commandapi.CommandComponent;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.BaseConfig;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.coinflip.CoinflipGameUserInterfaceItem;
import xyz.zeppelin.casino.game.crash.CrashGame;
import xyz.zeppelin.casino.game.crash.CrashGameUserInterfaceItem;
import xyz.zeppelin.casino.game.mines.MinesGameUserInterfaceItem;
import xyz.zeppelin.casino.game.slots.SlotsGameUserInterfaceItem;
import xyz.zeppelin.casino.game.wheel.WheelGameUserInterfaceItem;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.ui.CasinoUserInterface;
import xyz.zeppelin.casino.ui.GamePreferencesUserInterface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadCommand extends CommandComponent {

    protected MainConfig config;
    protected MessagesConfig messages;


    public ReloadCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        createCommand().register();

        this.config = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);
        this.messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
    }

    @Override
    public CommandAPICommand createCommand() {
        return new CommandAPICommand("casino-reload")
                .withShortDescription("Reload the casino configurations (not recommended).")
                .withPermission("zeppelincasino.admin")
                .executesPlayer((sender, args) -> {
                    Player player = sender;

                    List<BaseConfig> configs = List.of(config, messages);

                    try {
                        configs.forEach(BaseConfig::reload);
                        player.sendMessage(new Message(messages.getMessage("config-reload-success")).colorize().getMessage());
                        Bukkit.getLogger().info("Zeppelin Casino Configurations were reloaded successfully.");
                    } catch (Exception e) {
                        player.sendMessage(new Message(messages.getMessage("config-reload-failed")).colorize().getMessage());
                        Bukkit.getLogger().warning("Zeppelin Casino Configurations could not be reloaded.");
                    }
                });
    }

}
