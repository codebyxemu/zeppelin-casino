package xyz.zeppelin.casino.game;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a single player game session.
 */
@Getter
public abstract class SinglePlayerGameSession<T extends Game> extends BaseGameSession<T> {

    protected final Plugin plugin;
    protected final Player player;
    protected final PlayerBetManager betManager;

    public SinglePlayerGameSession(PlayerBetManager playerBetManager) {
        this.plugin = playerBetManager.getPlugin();
        this.player = playerBetManager.getPlayer();
        this.betManager = playerBetManager;
    }

    protected final void openSummaryUI(String gameName, boolean isWin, Consumer<PlayerBetManager> repeat) {
        GameSummaryUserInterface.open(betManager, repeat, gameName, isWin);
    }

    @Override
    public final List<Player> getPlayers() {
        return ImmutableList.of(player);
    }
}
