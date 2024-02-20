package xyz.zeppelin.casino.game.slots;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class SlotsGameController {

    private final SlotsGameSession session;
    private BukkitTask timerTask;
    private int ticks = 0;
    private int slowdown = 1;

    void processStart() {
        BukkitScheduler scheduler = session.getPlugin().getServer().getScheduler();
        timerTask = scheduler.runTaskTimerAsynchronously(session.getPlugin(), this::tick, 0, 1);
    }

    private void tick() {
        ticks++;
        if (ticks % 20 == 0) slowdown += 2;
        if (slowdown >= 10) {
            timerTask.cancel();
            timerTask = null;
            session.getPlugin().getServer().getScheduler().runTask(session.getPlugin(), this::processStop);
            return;
        }
        if (ticks % slowdown != 0) return;
        SlotsGame game = session.getGame();
        game.nextRound(ticks > 66 ? 2 : ticks > 33 ? 1 : 0);
        session.getUserInterface().render();
    }

    private void processStop() {
        if (timerTask != null) timerTask.cancel();
        SlotsGame game = session.getGame();
        if (game.hasStarted()) session.getBetManager().setMultiplier(game.getMultiplier());
        session.end();
    }

    void processClose() {
        processStop();
    }

    void start() {
        session.getGame().prepare();
        SlotsGameUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());
    }

    void stop() {
        SlotsGameUserInterface userInterface = session.getUserInterface();
        userInterface.close();
        userInterface.unregister();
        session.getGame().clear();
    }
}
