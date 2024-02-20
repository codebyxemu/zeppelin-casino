package xyz.zeppelin.casino.game.slots;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.util.List;

public class SlotsGame implements Game {

    private final List<SlotConfig> items;
    private Slot firstSlot;
    private Slot secondSlot;
    private Slot thirdSlot;
    private int rounds = 0;

    public SlotsGame(Config config) {
        this.items = config.items;
        this.firstSlot = randomSlot();
        this.secondSlot = randomSlot();
        this.thirdSlot = randomSlot();
    }

    public void nextRound(int keepSlots) {
        rounds++;
        if (keepSlots < 1) {
            firstSlot = randomSlot();
        }
        if (keepSlots < 2) {
            secondSlot = randomSlot();
        }
        if (keepSlots < 3) {
            thirdSlot = randomSlot();
        }
    }

    public Slot getSlot(int ordinal) {
        Preconditions.checkArgument(ordinal > 0 && ordinal < 4, "Ordinal must be between 1 and 3.");
        return switch (ordinal) {
            case 1 -> firstSlot;
            case 2 -> secondSlot;
            case 3 -> thirdSlot;
            default -> throw new IllegalStateException("Unexpected value: " + ordinal);
        };
    }

    public boolean isWin() {
        return firstSlot.equals(secondSlot) && secondSlot.equals(thirdSlot);
    }

    public BigDecimal getMultiplier() {
        return firstSlot.multiplier;
    }

    public boolean hasStarted() {
        return rounds > 0;
    }

    private Slot randomSlot() {
        Slot randomSlot = null;
        for (int i = 0; i < 1000; i++) { // For instead of while to avoid infinite loop in case of bad configuration.
            for (SlotConfig item : items) {
                boolean isPicked = Math.random() < item.chance().doubleValue();
                if (isPicked) {
                    randomSlot = new Slot(item.material(), item.multiplier());
                    break;
                }
            }
        }
        if (randomSlot == null) {
            throw new IllegalStateException("No slot could be generated in slots machine. The configuration is invalid.");
        }
        return randomSlot;
    }

    public record Config(List<SlotConfig> items, BigDecimal maxBet, BigDecimal minBet) {
    }

    public record Slot(Material material, BigDecimal multiplier) {
    }

    public record SlotConfig(Material material, BigDecimal chance, BigDecimal multiplier) {
    }
}
