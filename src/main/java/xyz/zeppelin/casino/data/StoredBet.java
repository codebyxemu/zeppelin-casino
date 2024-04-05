package xyz.zeppelin.casino.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoredBet {
    private UUID betId;
    private UUID player;
    private String game;
    private BigDecimal amount;
    private double multiplier;
    private boolean win;

    public static StoredBet createDefault(UUID player, String game, BigDecimal amount, double multiplier) {
        return new StoredBet(UUID.randomUUID(), player, game, amount, multiplier, multiplier > 1);
    }
}
