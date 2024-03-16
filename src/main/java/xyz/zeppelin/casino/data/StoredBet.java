package xyz.zeppelin.casino.data;

import java.util.UUID;

public class StoredBet {

    private UUID player;
    private String game;
    private double amount;
    private double multiplier;
    private boolean win;

    public StoredBet(UUID player, String game, double amount, double multiplier, boolean win) {
        this.player = player;
        this.game = game;
        this.amount = amount;
        this.multiplier = multiplier;
        this.win = win;
    }

}
