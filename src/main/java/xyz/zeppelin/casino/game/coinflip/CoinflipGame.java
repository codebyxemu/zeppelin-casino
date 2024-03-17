package xyz.zeppelin.casino.game.coinflip;

import lombok.Getter;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class CoinflipGame implements Game {

    @Getter
    private Side winnerSide;
    private Side pickedSide;

    void flip(Side pickedSide) {
        this.pickedSide = pickedSide;
        this.winnerSide = ThreadLocalRandom.current().nextDouble(1) < 0.5 ? Side.HEADS : Side.TAILS;
    }

    boolean hasStarted() {
        return winnerSide != null && pickedSide != null;
    }

    boolean isWin() {
        return hasStarted() && winnerSide == pickedSide;
    }

    BigDecimal getMultiplier() {
        return BigDecimal.ONE;
    }

    enum Side {HEADS, TAILS}

    public record Config(BigDecimal maxBet, BigDecimal minBet) {
    }
}
