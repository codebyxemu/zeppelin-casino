package xyz.zeppelin.casino.bridge;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryEconomyBridge implements EconomyBridge {

    private final Map<UUID, BigDecimal> balances = new ConcurrentHashMap<>();

    @Override
    public boolean withdraw(Player player, BigDecimal amount) {
        BigDecimal balance = balances.computeIfAbsent(player.getUniqueId(), uuid -> DEFAULT_BALANCE);
        BigDecimal balanceAfterWithdraw = balance.subtract(amount);
        if (balanceAfterWithdraw.compareTo(BigDecimal.ZERO) < 0) return false;
        balances.put(player.getUniqueId(), balanceAfterWithdraw);
        return true;
    }

    @Override
    public boolean deposit(Player player, BigDecimal amount) {
        BigDecimal balance = balances.computeIfAbsent(player.getUniqueId(), uuid -> DEFAULT_BALANCE);
        BigDecimal balanceAfterDeposit = balance.add(amount);
        balances.put(player.getUniqueId(), balanceAfterDeposit);
        return true;
    }

    @Override
    public boolean hasSufficientBalance(Player player, BigDecimal amount) {
        return getBalance(player).compareTo(amount) >= 0;
    }

    @Override
    public BigDecimal getBalance(Player player) {
        return balances.computeIfAbsent(player.getUniqueId(), uuid -> DEFAULT_BALANCE);
    }

    private static final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(10000);
}
