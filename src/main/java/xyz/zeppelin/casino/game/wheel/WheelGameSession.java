package xyz.zeppelin.casino.game.wheel;

import lombok.Getter;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

@Getter
public class WheelGameSession extends SinglePlayerGameSession<WheelGame> {

    private final MessagesConfig messagesConfig;
    private final MainConfig mainConfig;
    private final WheelGame game;
    private final WheelGameUserInterface userInterface;
    private final WheelGameController controller;

    private WheelGameSession(PlayerBetManager playerBetManager) {
        super(playerBetManager);
        this.messagesConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MessagesConfig.class);
        this.mainConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MainConfig.class);
        this.game = new WheelGame(mainConfig.getWheelConfig());
        this.userInterface = new WheelGameUserInterface(this);
        this.controller = new WheelGameController(this);
    }

    void end() {
        state = State.Finished;
        controller.stop();

        if (!game.hasStarted()) {
            refund();
            return;
        }

        if (game.isWin()) betManager.giveWinning();
        openSummaryUI("Wheel of Fortune", game.isWin(), WheelGameSession::start);
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
        new WheelGameSession(betManager).start();
    }
}
