package xyz.zeppelin.casino.game.crash;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import xyz.zeppelin.casino.ui.InventoryUserInterface;

@RequiredArgsConstructor
public class CrashGameController {

    private final CrashGameSession session;
    private BukkitTask timerTask;

    void processStart() {
        BukkitScheduler scheduler = session.getPlugin().getServer().getScheduler();
        timerTask = scheduler.runTaskTimerAsynchronously(session.getPlugin(), this::tick, 0, 1);
    }

    private void tick() {
        CrashGame game = session.getGame();
        game.grow();
        session.getUserInterface().render();
        if (game.isLose()) {
            timerTask.cancel();
            BukkitScheduler scheduler = session.getPlugin().getServer().getScheduler();
            scheduler.runTask(session.getPlugin(), session::end);
        }
    }

    void processClose() {
        processStop();
    }

    void processStop() {
        if (timerTask != null) timerTask.cancel();
        session.getBetManager().addMultiplier(session.getGame().getMultiplier());
        session.end();
    }

    void start() {
        session.getGame().prepare();
        InventoryUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());
    }

    void stop() {
        InventoryUserInterface userInterface = session.getUserInterface();
        userInterface.close();
        userInterface.unregister();
        session.getGame().clear();
    }
}
