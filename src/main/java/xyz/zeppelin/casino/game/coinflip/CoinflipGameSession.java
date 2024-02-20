package xyz.zeppelin.casino.game.coinflip;

import lombok.Getter;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

@Getter
public class CoinflipGameSession extends SinglePlayerGameSession<CoinflipGame> {

    private final MessagesConfig messagesConfig;
    private final CoinflipGameUserInterface userInterface;
    private final CoinflipGameController controller;
    private final PlayerBetManager betManager;
    private final CoinflipGame game;

    private CoinflipGameSession(PlayerBetManager betManager) {
        super(betManager);
        this.messagesConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MessagesConfig.class);
        this.betManager = betManager;
        this.game = new CoinflipGame();
        this.userInterface = new CoinflipGameUserInterface(this);
        this.controller = new CoinflipGameController(this);
    }


    void end() {
        state = State.Finished;
        controller.stop();

        if (!game.hasStarted()) {
            refund();
            return;
        }

        if (game.isWin()) betManager.giveWinning();
        openSummaryUI("Coinflip", game.isWin(), CoinflipGameSession::start);
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
        new CoinflipGameSession(betManager).start();
    }
}
