package xyz.zeppelin.casino.bridge.database;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.bridge.DatabaseBridge;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.DatabaseConfig;
import xyz.zeppelin.casino.data.StoredBet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class FlatFileDatabaseBridge implements DatabaseBridge {

    protected Plugin plugin;
    private final DatabaseConfig config;

    public FlatFileDatabaseBridge(Plugin plugin) {
        this.plugin = plugin;
        Validate.notNull(plugin, "plugin cannot be null");

        this.config = ComponentManager.getComponentManager(plugin).getComponent(DatabaseConfig.class);
    }

    @Override
    public int getWins(Player player) {
        return getBets(bet -> bet.getPlayer().equals(player.getUniqueId()) && bet.isWin()).size();
    }

    @Override
    public int getLosses(Player player) {
        return getBets(bet -> bet.getPlayer().equals(player.getUniqueId()) && !bet.isWin()).size();
    }

    @Override
    public BigDecimal getTotalWagered(Player player) {
        return getBets(bet -> bet.getPlayer().equals(player.getUniqueId())).stream()
                .map(StoredBet::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void addBet(StoredBet bet) {
        Validate.notNull(bet, "bet must contain a value");

        config.getYamlConfig().set("bets." + bet.getBetId(), null);
        config.getYamlConfig().set("bets." + bet.getBetId() + ".player", bet.getPlayer().toString());
        config.getYamlConfig().set("bets." + bet.getBetId() + ".game", bet.getGame());
        config.getYamlConfig().set("bets." + bet.getBetId() + ".amount", bet.getAmount().toString());
        config.getYamlConfig().set("bets." + bet.getBetId() + ".multiplier", bet.getMultiplier());
        config.getYamlConfig().set("bets." + bet.getBetId() + ".win", bet.isWin());

        config.save();
    }

    @Override
    public List<StoredBet> getBets(Predicate<StoredBet> filter) {
        return allBets().stream().filter(filter).toList();
    }

    @Override
    public StoredBet getBet(UUID uuid) {
        return getBets(bet -> bet.getBetId().equals(uuid)).stream().findFirst().orElse(null);
    }

    @Override
    public List<StoredBet> allBets() {
        ArrayList<StoredBet> bets = new ArrayList<>();

        if (config.getYamlConfig().getConfigurationSection("bets") == null) {
            return bets;
        }

        config.getYamlConfig().getConfigurationSection("bets").getKeys(false).forEach(betId -> {
            Validate.notNull(betId, "betId cannot be null");

            UUID player = UUID.fromString(config.getYamlConfig().getString("bets." + betId + ".player"));
            String game = config.getYamlConfig().getString("bets." + betId + ".game");
            BigDecimal amount = new BigDecimal(config.getYamlConfig().getString("bets." + betId + ".amount"));
            double multiplier = config.getYamlConfig().getDouble("bets." + betId + ".multiplier");
            boolean win = config.getYamlConfig().getBoolean("bets." + betId + ".win");

            bets.add(new StoredBet(UUID.fromString(betId), player, game, amount, multiplier, win));
        });

        return bets;
    }

    public static FlatFileDatabaseBridge create(Plugin plugin) {
        return new FlatFileDatabaseBridge(plugin);
    }
}
