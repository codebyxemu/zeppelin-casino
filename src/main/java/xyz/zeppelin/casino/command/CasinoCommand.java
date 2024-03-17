package xyz.zeppelin.casino.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.commandapi.CommandComponent;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.coinflip.CoinflipGameUserInterfaceItem;
import xyz.zeppelin.casino.game.crash.CrashGame;
import xyz.zeppelin.casino.game.crash.CrashGameUserInterfaceItem;
import xyz.zeppelin.casino.game.mines.MinesGameUserInterfaceItem;
import xyz.zeppelin.casino.game.slots.SlotsGameUserInterfaceItem;
import xyz.zeppelin.casino.game.wheel.WheelGameUserInterfaceItem;
import xyz.zeppelin.casino.ui.CasinoUserInterface;
import xyz.zeppelin.casino.ui.GamePreferencesUserInterface;

import java.math.BigDecimal;
import java.util.Arrays;

public class CasinoCommand extends CommandComponent {

    protected MainConfig config;

    protected CrashGameUserInterfaceItem crash;
    protected CoinflipGameUserInterfaceItem coinFlip;
    protected MinesGameUserInterfaceItem mines;
    protected SlotsGameUserInterfaceItem slots;
    protected WheelGameUserInterfaceItem wheel;


    public CasinoCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        crash = new CrashGameUserInterfaceItem(plugin);
        coinFlip = new CoinflipGameUserInterfaceItem(plugin);
        mines = new MinesGameUserInterfaceItem(plugin);
        slots = new SlotsGameUserInterfaceItem(plugin);
        wheel = new WheelGameUserInterfaceItem(plugin);

        createCommand().register();

        this.config = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);
    }

    @Override
    public CommandAPICommand createCommand() {
        return new CommandAPICommand("casino")
                .withAliases("zeppelin-casino")
                .withShortDescription("Open the virtual casino.")
                .withOptionalArguments(Arrays.asList(
                        new StringArgument("game").setOptional(true)
                ))
                .executesPlayer((sender, args) -> {
                    Player player = sender;

                    CasinoUserInterface.open(plugin, sender);

                    if (args.get(0) != null) {
                        String game = ((String) args.get(0)).toLowerCase();

                        switch (game) {
                            case "crash":
                                if (!config.gameStatus("crash")) {
                                    MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                    player.sendMessage(messages.getMessage("game-does-not-exist"));
                                    return;
                                }
                                crash.quickOpen(player);
                                break;
                            case "wheel":
                                if (!config.gameStatus("wheel")) {
                                    MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                    player.sendMessage(messages.getMessage("game-does-not-exist"));
                                    return;
                                }
                                wheel.quickOpen(player);
                                break;
                            case "slots":
                                if (!config.gameStatus("slots")) {
                                    MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                    player.sendMessage(messages.getMessage("game-does-not-exist"));
                                    return;
                                }
                                slots.quickOpen(player);
                                break;
                            case "mines":
                                if (!config.gameStatus("mines")) {
                                    MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                    player.sendMessage(messages.getMessage("game-does-not-exist"));
                                    return;
                                }
                                mines.quickOpen(player);
                                break;
                            case "coinflip":
                                if (!config.gameStatus("coinflip")) {
                                    MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                    player.sendMessage(messages.getMessage("game-does-not-exist"));
                                    return;
                                }
                                coinFlip.quickOpen(player);
                                break;
                            default:
                                MessagesConfig messages = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
                                player.sendMessage(messages.getMessage("game-does-not-exist"));
                                break;
                        }
                    }
                });
    }

}
