package xyz.zeppelin.casino.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.commandapi.CommandComponent;
import xyz.zeppelin.casino.component.ComponentManager;
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
                                crash.quickOpen(player);
                                break;
                            case "wheel":
                                wheel.quickOpen(player);
                                break;
                            case "slots":
                                slots.quickOpen(player);
                                break;
                            case "mines":
                                mines.quickOpen(player);
                                break;
                            case "coinflip":
                                coinFlip.quickOpen(player);
                                break;
                        }
                    }
                });
    }

}
