package xyz.zeppelin.casino.game.crash;

import lombok.Getter;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class CrashGame implements Game {

    private final BigDecimal maxMultiplier;
    private final BigDecimal crashChance;
    private final BigDecimal baseMultiplier;
    @Getter
    private BigDecimal multiplier = BigDecimal.ZERO;
    private boolean crashed = false;

    public CrashGame(CrashGame.Config config) {
        this.maxMultiplier = config.maxMultiplier;
        this.crashChance = config.crashChance;
        this.baseMultiplier = config.baseMultiplier;
    }

    void grow() {
        if (crashed) return;
        if (multiplier.compareTo(maxMultiplier) >= 0) return;
        BigDecimal speedUp = BigDecimal.ONE.add(multiplier);
        BigDecimal toAdd = baseMultiplier.multiply(speedUp);
        BigDecimal newMultiplier = multiplier.add(toAdd);
        if (newMultiplier.compareTo(maxMultiplier) > 0) {
            multiplier = maxMultiplier;
        } else {
            multiplier = newMultiplier;
        }
        boolean shouldCrash = ThreadLocalRandom.current().nextDouble() < crashChance.doubleValue();
        if (shouldCrash) crashed = true;
    }

    boolean hasStarted() {
        return multiplier.compareTo(BigDecimal.ZERO) > 0;
    }

    boolean isWin() {
        return hasStarted() && !crashed;
    }

    boolean isLose() {
        return crashed;
    }

    public record Config(
            BigDecimal maxMultiplier,
            BigDecimal baseMultiplier,
            BigDecimal crashChance,
            BigDecimal maxBet,
            BigDecimal minBet
    ) {
    }
}
