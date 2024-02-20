package xyz.zeppelin.casino.game.wheel;

import lombok.Getter;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WheelGame implements Game {

    @Getter
    private final List<BigDecimal> wheel;
    private int positionIndex = 0;
    private int spins = 0;

    public WheelGame(Config config) {
        this.wheel = new ArrayList<>(config.items);
        Collections.shuffle(wheel);
    }

    BigDecimal getPosition() {
        return wheel.get(positionIndex);
    }

    BigDecimal getOffsetPosition(int offset) {
        if (positionIndex + offset < 0) {
            return wheel.get(wheel.size() + (positionIndex + offset));
        } else if (positionIndex + offset >= wheel.size()) {
            return wheel.get((positionIndex + offset) - wheel.size());
        } else {
            return wheel.get(positionIndex + offset);
        }
    }

    void spin() {
        spins++;
        if (positionIndex + 1 < wheel.size()) {
            positionIndex++;
        } else {
            positionIndex = 0;
        }
    }

    boolean hasStarted() {
        return spins > 0;
    }

    boolean isWin() {
        return hasStarted() && getPosition().compareTo(BigDecimal.ZERO) > 0;
    }

    public record Config(List<BigDecimal> items) {
    }
}
