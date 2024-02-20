package xyz.zeppelin.casino.game.crash;

import lombok.Getter;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

@Getter
public class CrashGameSession extends SinglePlayerGameSession<CrashGame> {

    private final MainConfig mainConfig;
    private final MessagesConfig messagesConfig;
    private final CrashGameUserInterface userInterface;
    private final CrashGameController controller;
    private final PlayerBetManager betManager;
    private final CrashGame game;

    private CrashGameSession(PlayerBetManager betManager) {
        super(betManager);
        this.messagesConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MessagesConfig.class);
        this.mainConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MainConfig.class);
        this.betManager = betManager;
        this.game = new CrashGame(mainConfig.getCrashConfig());
        this.userInterface = new CrashGameUserInterface(this);
        this.controller = new CrashGameController(this);
    }


    void end() {
        state = State.Finished;
        controller.stop();

        if (!game.hasStarted()) {
            refund();
            return;
        }

        if (game.isWin()) betManager.giveWinning();
        openSummaryUI("Crash", game.isWin(), CrashGameSession::start);
    }

    private void refund() {
        betManager.returnBet();
        player.sendMessage(messagesConfig.getRefundMessage());

    }

    @Override
    protected void onStart() {
        controller.start();
    }

    @Override
    protected void onStop() {
        controller.stop();
    }

    public static void start(PlayerBetManager betManager) {
        boolean isBetPlaced = betManager.placeBet();
        if (!isBetPlaced) {
            MessagesConfig messagesConfig = ComponentManager.getComponentManager(betManager.getPlugin()).getComponent(MessagesConfig.class);
            betManager.getPlayer().sendMessage(messagesConfig.getInsufficientBalanceToBet());
            return;
        }
        new CrashGameSession(betManager).start();
    }
}
