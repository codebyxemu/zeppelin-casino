package xyz.zeppelin.casino;

import dev.demeng.sentinel.wrapper.SentinelClient;
import dev.demeng.sentinel.wrapper.exception.ApiException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.bridge.database.FlatFileDatabaseBridge;
import xyz.zeppelin.casino.bstats.BstatsComponent;
import xyz.zeppelin.casino.command.CasinoCommand;
import xyz.zeppelin.casino.command.ReloadCommand;
import xyz.zeppelin.casino.commandapi.CommandApiComponent;
import xyz.zeppelin.casino.common.Environment;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.DatabaseConfig;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ZeppelinCasinoPlugin extends JavaPlugin {

    private final ComponentManager componentManager = ComponentManager.register(this, List.of(
            MainConfig.createDefault(this),
            MessagesConfig.createDefault(this),
            DatabaseConfig.createDefault(this),
            new CommandApiComponent(this),
            new BstatsComponent(this),
            new CasinoCommand(this),
            new ReloadCommand(this),
            EconomyBridge.createDetected(this),
            FlatFileDatabaseBridge.create(this)
    ));

    @Override
    public void onLoad() {
        if (Environment.isDevelopmentMode()) {
            getLogger().warning("[ZeppelinCasino] Development mode is enabled. Be careful this may be dangerous for production environments!");
        }
        componentManager.loadComponents();
    }

    @Override
    public void onEnable() {
        componentManager.enableComponents();

        Bukkit.getLogger().info("[ZeppelinCasino] Welcome to Zeppelin Casino v" + getDescription().getVersion() + ".");

        // Manage the FlatFileDatabaseBridge
        FlatFileDatabaseBridge databaseBridge = ComponentManager.getComponentManager(this).getComponent(FlatFileDatabaseBridge.class);
        databaseBridge.onEnable();
    }


    @Override
    public void onDisable() {
        componentManager.disableComponents();
    }

}
