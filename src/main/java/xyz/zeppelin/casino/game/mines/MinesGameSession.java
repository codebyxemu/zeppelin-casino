package xyz.zeppelin.casino.game.mines;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

@Getter
public class MinesGameSession extends SinglePlayerGameSession<MinesGame> {

    private final MinesGameController controller;
    private final MinesGameUserInterface userInterface;
    private final Plugin plugin;

    private MinesGameSession(Plugin plugin, Player player, int height, float multiplier, BigDecimal bet) {
        super(new MinesGame(9, height, multiplier, bet), player);
        this.plugin = plugin;
        this.controller = new MinesGameController(this);
        this.userInterface = new MinesGameUserInterface(this, height);
    }

    @Override
    protected void onStart() {
        controller.start();
    }

    @Override
    protected void onStop() {
        controller.stop();
    }

    void end() {
        state = State.Finished;
        userInterface.close();
        controller.stop();
        if (!game.isStarted()) {
            refund();
            return;
        }
        if (game.isWon()) {
            reward();
        } else {
            cheer();
        }
    }

    private void refund() {
        // ToDo: Get the message from config
        getPlayer().sendMessage("Game was not started, your bet was refunded.");
        EconomyBridge economyBridge = ((ZeppelinCasinoPlugin) plugin).getEconomyBridge();
        economyBridge.deposit(getPlayer(), game.getBet());
    }

    private void reward() {
        // ToDo: Get the message from config
        String formattedAmount = DecimalFormat.getCurrencyInstance(Locale.US).format(game.getBet());
        getPlayer().sendMessage("Congratulations, your win is " + formattedAmount + ", keep it up!");
    }

    private void cheer() {
        // ToDo: Get the message from config
        getPlayer().sendMessage("Unfortunately, you lost. Better luck next time!");
    }

    public static void start(Plugin plugin, Player player, BigDecimal bet, Difficulty difficulty) {
        Preconditions.checkArgument(bet.compareTo(BigDecimal.ZERO) > 0, "Bet must be positive");
        float multiplier = switch (difficulty) {
            case EASY -> 1f;
            case NORMAL -> 2f;
            case HARD -> 3f;
        };
        new MinesGameSession(plugin, player, 6, multiplier, bet).start();
    }

    public enum Difficulty {EASY, NORMAL, HARD}
}
