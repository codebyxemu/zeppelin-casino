package xyz.zeppelin.casino.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.game.Game;
import xyz.zeppelin.casino.game.coinflip.CoinflipGame;
import xyz.zeppelin.casino.game.crash.CrashGame;
import xyz.zeppelin.casino.game.mines.MinesGame;
import xyz.zeppelin.casino.game.slots.SlotsGame;
import xyz.zeppelin.casino.game.wheel.WheelGame;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class BetsConfig extends BaseConfig {

    public BetsConfig(File file, String defaultName, Logger logger) {
        super(file, defaultName, logger);
    }

    public static MainConfig createDefault(Plugin plugin) {
        return new MainConfig(new File(plugin.getDataFolder(), "storage.yml"), "/config/storage.yml", plugin.getLogger());
    }
}
