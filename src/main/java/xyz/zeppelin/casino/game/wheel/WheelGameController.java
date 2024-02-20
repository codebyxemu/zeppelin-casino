package xyz.zeppelin.casino.game.wheel;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class WheelGameController {

    private final WheelGameSession session;
    private int slowdown = 1;
    private int ticks = 0;
    private BukkitTask timerTask;

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
            BukkitScheduler scheduler = session.getPlugin().getServer().getScheduler();
            scheduler.runTask(session.getPlugin(), this::processStop);
            return;
        }
        if (ticks % slowdown != 0) return;
        session.getGame().spin();
        session.getUserInterface().render();
    }

    void processClose() {
        processStop();
    }

    private void processStop() {
        if (timerTask != null) timerTask.cancel();
        WheelGame game = session.getGame();
        if (game.hasStarted()) session.getBetManager().setMultiplier(game.getPosition());
        session.end();
    }

    void start() {
        session.getGame().prepare();
        WheelGameUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());
    }

    void stop() {
        WheelGameUserInterface userInterface = session.getUserInterface();
        userInterface.close();
        userInterface.unregister();
        session.getGame().clear();
    }
}
