package xyz.zeppelin.casino.game.mines;

import com.google.common.base.Preconditions;
import lombok.Getter;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MinesGame implements Game {

    private static final MathContext MATH_CONTEXT = new MathContext(3, RoundingMode.DOWN);
    private final BigDecimal baseMineChance;
    private final BigDecimal baseMinMultiplier;
    private final BigDecimal baseMaxMultiplier;
    private final GameField[][] fields;
    private final BigDecimal multiplier;
    private final GameField[][] openedFields;
    @Getter
    private final Difficulty difficulty;
    private boolean lost = false;

    MinesGame(Config config, Game.Difficulty difficulty, int width, int height) {
        Preconditions.checkArgument(width > 0, "Width must be positive");
        Preconditions.checkArgument(height > 0, "Height must be positive");
        this.difficulty = difficulty;
        this.fields = new GameField[width][height];
        this.openedFields = new GameField[width][height];
        this.multiplier = config.difficultyMultipliers.get(difficulty);
        this.baseMineChance = config.baseMineChance;
        this.baseMinMultiplier = config.baseMinMultiplier;
        this.baseMaxMultiplier = config.baseMaxMultiplier;
    }

    /**
     * Opens a field at the given position.
     *
     * @param x The x position of the field.
     * @param y The y position of the field.
     * @return The field that was opened, or null if the field was already opened.
     */
    public GameField openField(int x, int y) {
        Preconditions.checkArgument(x >= 0 && x < fields.length, "Width out of bounds");
        Preconditions.checkArgument(y >= 0 && y < fields[x].length, "Height out of bounds");
        if (isFieldOpened(x, y)) return null;
        GameField field = fields[x][y];
        if (field == null) return null;
        openedFields[x][y] = field;
        processField(field);
        return field;
    }

    private void processField(GameField field) {
        if (field.isMine()) lost = true;
    }

    private boolean isFieldOpened(int width, int height) {
        return openedFields[width][height] != null;
    }

    public boolean hasGameEnded() {
        return lost || isNoHiddenFields();
    }

    private boolean isNoHiddenFields() {
        return Arrays.stream(openedFields).flatMap(Arrays::stream).allMatch(Objects::nonNull);
    }

    public boolean hasStarted() {
        return Arrays.stream(openedFields).anyMatch(Objects::nonNull);
    }

    public boolean isWin() {
        return !lost;
    }

    @Override
    public void prepare() {
        generateFields();
    }

    private void generateFields() {
        BigDecimal mineChance = baseMineChance.multiply(multiplier, MATH_CONTEXT);
        BigDecimal minMultiplier = baseMinMultiplier.multiply(multiplier, MATH_CONTEXT);
        BigDecimal maxMultiplier = baseMaxMultiplier.multiply(multiplier, MATH_CONTEXT);
        fillFields(() -> {
            boolean isMine = mineChance.compareTo(BigDecimal.valueOf(Math.random())) >= 0;
            if (isMine) {
                return MineField.INSTANCE;
            } else {
                BigDecimal random = BigDecimal.valueOf(Math.random());
                BigDecimal multiplier = random.multiply(maxMultiplier.subtract(minMultiplier)).add(minMultiplier);
                return new MultiplierField(multiplier);
            }
        });
    }

    private void fillFields(Supplier<GameField> function) {
        for (int width = 0; width < fields.length; width++) {
            for (int height = 0; height < fields[width].length; height++) {
                fields[width][height] = function.get();
            }
        }
    }

    public record Config(
            BigDecimal baseMineChance,
            BigDecimal baseMinMultiplier,
            BigDecimal baseMaxMultiplier,
            Map<Game.Difficulty, BigDecimal> difficultyMultipliers
    ) {
    }

    public sealed interface GameField {
        default boolean isMine() {
            return this instanceof MineField;
        }
    }

    public record MultiplierField(BigDecimal multiplier) implements GameField {
    }

    public static final class MineField implements GameField {
        private static final MineField INSTANCE = new MineField();
    }
}
