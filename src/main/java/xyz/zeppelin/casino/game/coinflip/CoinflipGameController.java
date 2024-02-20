package xyz.zeppelin.casino.game.coinflip;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class CoinflipGameController {

    private final CoinflipGameSession session;
    private BukkitTask laterTask;

    void processPickSide(CoinflipGame.Side side) {
        CoinflipGame game = session.getGame();
        game.flip(side);
        BukkitScheduler scheduler = session.getPlugin().getServer().getScheduler();
        BukkitTask timerTask = scheduler.runTaskTimerAsynchronously(session.getPlugin(), () -> session.getUserInterface().playAnimation(), 0, 1);
        laterTask = scheduler.runTaskLater(session.getPlugin(), () -> processFlip(timerTask), 80);
    }

    private void processFlip(BukkitTask timerTask) {
        timerTask.cancel();
        CoinflipGame game = session.getGame();
        if (game.isWin()) session.getBetManager().addMultiplier(game.getMultiplier());
        session.end();
    }

    void processClose() {
        session.end();
    }

    void start() {
        session.getGame().prepare();
        CoinflipGameUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());
    }

    void stop() {
        if (laterTask != null && !laterTask.isCancelled()) laterTask.cancel();
        CoinflipGameUserInterface userInterface = session.getUserInterface();
        userInterface.close();
        userInterface.unregister();
        session.getGame().clear();
    }
}
