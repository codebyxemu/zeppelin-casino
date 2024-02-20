package xyz.zeppelin.casino.game.mines;

import lombok.Getter;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.Game;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.game.SinglePlayerGameSession;

@Getter
public class MinesGameSession extends SinglePlayerGameSession<MinesGame> {

    private final MessagesConfig messagesConfig;
    private final MainConfig mainConfig;
    private final MinesGameController controller;
    private final MinesGameUserInterface userInterface;
    private final MinesGame game;

    private MinesGameSession(PlayerBetManager betManager, Game.Difficulty difficulty) {
        super(betManager);
        this.mainConfig = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);
        this.game = new MinesGame(mainConfig.getMinesConfig(), difficulty, 4, 4);
        this.messagesConfig = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
        this.userInterface = new MinesGameUserInterface(this);
        this.controller = new MinesGameController(this);
    }

    @Override
    protected void onStart() {
        controller.start();
    }

    @Override
    protected void onStop() {
        controller.stop();
    }

    void end() {
        state = State.Finished;
        controller.stop();

        if (!game.hasStarted()) {
            refund();
            return;
        }

        if (game.isWin()) betManager.giveWinning();
        openSummaryUI("Mines", game.isWin(), (betManager) -> start(betManager, game.getDifficulty()));
    }

    private void refund() {
        betManager.returnBet();
        player.sendMessage(messagesConfig.getRefundMessage());

    }

    public static void start(PlayerBetManager betManager, Game.Difficulty difficulty) {
        boolean isBetPlaced = betManager.placeBet();
        if (!isBetPlaced) {
            MessagesConfig messagesConfig = ComponentManager.getComponentManager(betManager.getPlugin()).getComponent(MessagesConfig.class);
            betManager.getPlayer().sendMessage(messagesConfig.getInsufficientBalanceToBet());
            return;
        }
        new MinesGameSession(betManager, difficulty).start();
    }
}
