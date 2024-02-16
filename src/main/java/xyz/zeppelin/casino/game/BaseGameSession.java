package xyz.zeppelin.casino.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseGameSession<T extends Game> implements GameSession<T> {

    protected final T game;
    protected State state = State.Idle;

    @Override
    public final boolean start() {
        if (state != State.Idle) return false;
        state = State.Going;
        onStart();
        return true;
    }

    protected abstract void onStart();

    @Override
    public final boolean stop() {
        if (state != State.Going) return false;
        state = State.Stopped;
        onStop();
        return true;
    }

    protected abstract void onStop();
}
