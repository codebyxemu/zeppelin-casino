package xyz.zeppelin.casino.game.mines;

import com.google.common.base.Preconditions;
import lombok.Getter;
import xyz.zeppelin.casino.game.Game;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class MinesGame implements Game {

    private static final BigDecimal BASE_MINE_CHANCE = BigDecimal.valueOf(0.3);
    private static final BigDecimal BASE_MIN_MULTIPLIER = BigDecimal.valueOf(1.1);
    private static final BigDecimal BASE_MAX_MULTIPLIER = BigDecimal.valueOf(2.0);
    private static final MathContext MATH_CONTEXT = new MathContext(3, RoundingMode.DOWN);
    @Getter
    private BigDecimal bet;
    private final GameField[][] fields;
    private final BigDecimal multiplier;
    private final GameField[][] openedFields;
    @Getter
    private boolean lost = false;

    MinesGame(int width, int height, float multiplier, BigDecimal bet) {
        this.fields = new GameField[width][height];
        this.openedFields = new GameField[width][height];
        this.multiplier = BigDecimal.valueOf(multiplier);
        this.bet = bet;
    }

    public GameField openField(int width, int height) {
        Preconditions.checkArgument(width >= 0 && width < fields.length, "Width out of bounds");
        Preconditions.checkArgument(height >= 0 && height < fields[width].length, "Height out of bounds");
        if (isFieldOpened(width, height)) return null;
        GameField field = fields[width][height];
        if (field == null) return null;
        openedFields[width][height] = field;
        processField(field);
        return field;
    }

    private void processField(GameField field) {
        if (field.isMine()) lost = true;
        else multiplyBetByField(field);
    }

    private void multiplyBetByField(GameField field) {
        BigDecimal fieldMultiplier = ((MultiplierField) field).multiplier;
        bet = bet.multiply(fieldMultiplier, MATH_CONTEXT);
    }

    private boolean isFieldOpened(int width, int height) {
        return openedFields[width][height] != null;
    }

    public boolean isWon() {
        return Arrays.stream(openedFields).allMatch(Objects::nonNull);
    }

    @Override
    public void prepare() {
        generateFields();
    }

    private void generateFields() {
        BigDecimal mineChance = BASE_MINE_CHANCE.multiply(multiplier, MATH_CONTEXT);
        BigDecimal minMultiplier = BASE_MIN_MULTIPLIER.multiply(multiplier, MATH_CONTEXT);
        BigDecimal maxMultiplier = BASE_MAX_MULTIPLIER.multiply(multiplier, MATH_CONTEXT);
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
