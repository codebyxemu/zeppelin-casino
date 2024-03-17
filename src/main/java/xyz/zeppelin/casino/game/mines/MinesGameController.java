package xyz.zeppelin.casino.game.mines;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinesGameController {

    private final MinesGameSession session;

    MinesGame.GameField processFieldClick(int x, int y) {
        MinesGame game = session.getGame();
        MinesGame.GameField field = game.openField(x, y);
        if (field == null) return null;
        if (!field.isMine()) {
            MinesGame.MultiplierField multiplierField = (MinesGame.MultiplierField) field;
            session.getBetManager().addMultiplier(multiplierField.multiplier());
        }
        if (game.hasGameEnded()) session.end();
        session.getUserInterface().updateTitle();
        return field;
    }

    void processClose() {
        session.end();
    }

    void start() {
        session.getBetManager().reset();
        session.getGame().prepare();
        MinesGameUserInterface userInterface = session.getUserInterface();
        userInterface.render();
        userInterface.register();
        userInterface.show(session.getPlayer());

        userInterface.updateTitle();
    }

    void stop() {
        MinesGameUserInterface userInterface = session.getUserInterface();
        userInterface.close();
        userInterface.unregister();
        session.getGame().clear();
    }
}
