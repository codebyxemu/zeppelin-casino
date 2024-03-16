package xyz.zeppelin.casino.game;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Represents a single player game session.
 */
@Getter
public abstract class SinglePlayerGameSession<T extends Game> extends BaseGameSession<T> {

    protected final Plugin plugin;
    protected final Player player;
    protected final PlayerBetManager betManager;

    protected MainConfig mainConfig;

    protected boolean bigWinAnnounce;
    protected double bigWinMultiplier;

    public SinglePlayerGameSession(PlayerBetManager playerBetManager) {
        this.plugin = playerBetManager.getPlugin();
        this.player = playerBetManager.getPlayer();
        this.betManager = playerBetManager;

        this.mainConfig = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);

        this.bigWinAnnounce = mainConfig.isBigWinAnnounce();
        this.bigWinMultiplier = mainConfig.getBigWinMultiplier();
    }

    protected final void openSummaryUI(String gameName, boolean isWin, Consumer<PlayerBetManager> repeat) {
        GameSummaryUserInterface.open(betManager, repeat, gameName, isWin);

        if (bigWinAnnounce) {

            if (isWin && betManager.getMultiplier().compareTo(BigDecimal.valueOf(bigWinMultiplier)) >= 0) {
                String formattedWinning = DecimalFormat.getCurrencyInstance(Locale.US).format(betManager.calculateWinning());

                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                String command = "/" + mainConfig.getBigWinAnnounce(
                        player.getName(),
                        gameName,
                        formattedWinning,
                        betManager.getMultiplier().doubleValue()
                );


                Bukkit.dispatchCommand(console, command);

            }
        }

    }

    @Override
    public final List<Player> getPlayers() {
        return ImmutableList.of(player);
    }
}
