package xyz.zeppelin.casino.game;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a single player game session.
 */
@Getter
public abstract class SinglePlayerGameSession<T extends Game> extends BaseGameSession<T> {

    private final Player player;

    public SinglePlayerGameSession(T game, Player player) {
        super(game);
        this.player = player;
    }

    @Override
    public final List<Player> getPlayers() {
        return ImmutableList.of(player);
    }
}
