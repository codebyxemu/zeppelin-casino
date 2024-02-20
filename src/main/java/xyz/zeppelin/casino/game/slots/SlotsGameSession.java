package xyz.zeppelin.casino.game.slots;

import lombok.Getter;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

@Getter
public class SlotsGameSession extends SinglePlayerGameSession<SlotsGame> {

    private final MainConfig mainConfig;
    private final MessagesConfig messagesConfig;
    private final SlotsGame game;
    private final SlotsGameController controller;
    private final SlotsGameUserInterface userInterface;

    private SlotsGameSession(PlayerBetManager playerBetManager) {
        super(playerBetManager);
        this.mainConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MainConfig.class);
        this.messagesConfig = ComponentManager.getComponentManager(getPlugin()).getComponent(MessagesConfig.class);
        this.game = new SlotsGame(mainConfig.getSlotsConfig());
        this.userInterface = new SlotsGameUserInterface(this);
        this.controller = new SlotsGameController(this);
    }

    public void end() {
        state = State.Finished;
        controller.stop();

        if (!game.hasStarted()) {
            refund();
            return;
        }

        if (game.isWin()) betManager.giveWinning();
        openSummaryUI("Slots", game.isWin(), SlotsGameSession::start);
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
        new SlotsGameSession(betManager).start();
    }
}
