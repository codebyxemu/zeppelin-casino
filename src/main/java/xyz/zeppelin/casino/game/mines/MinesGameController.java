package xyz.zeppelin.casino.game.mines;

public class MinesGameController {

    private final MinesGameSession session;

    MinesGameController(MinesGameSession session) {
        this.session = session;
    }

    MinesGame.GameField processFieldClick(int x, int y) {
        MinesGame game = session.getGame();
        MinesGame.GameField field = game.openField(x, y);
        if (game.hasGameEnded()) session.end();
        session.getUserInterface().updateTitle();
        return field;
    }

    void processClose() {
        session.end();
    }

    void start() {
        session.getGame().prepare();
        MinesGameUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());
    }

    void stop() {
        MinesGameUserInterface userInterface = session.getUserInterface();
        userInterface.close(session.getPlayer());
        userInterface.unregister();
        session.getGame().clear();
    }
}
