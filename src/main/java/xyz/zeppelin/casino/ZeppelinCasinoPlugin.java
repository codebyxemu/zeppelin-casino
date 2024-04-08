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
            getLogger().warning("Development mode is enabled. Be careful this may be dangerous for production environments!");
        }
        componentManager.loadComponents();
    }

    @Override
    public void onEnable() {
        componentManager.enableComponents();

        Bukkit.getLogger().info("Welcome to Zeppelin Casino v" + getDescription().getVersion() + ".");

        if (authenticate()) {
            Bukkit.getLogger().info("Your license was confirmed. Thanks for your purchase!");
        } else {
            Bukkit.getLogger().warning("You do not have a valid license for Zeppelin Casino. " +
                    "The plugin will shut down now. You can receive a license in our Discord server.");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Manage the FlatFileDatabaseBridge
        FlatFileDatabaseBridge databaseBridge = ComponentManager.getComponentManager(this).getComponent(FlatFileDatabaseBridge.class);
        databaseBridge.onEnable();

    }


    @Override
    public void onDisable() {
        componentManager.disableComponents();
    }

    private boolean authenticate() {
        getLogger().info("[ZeppelinCasino] Loading Licensing System...");

        String licenseKey = ComponentManager.getComponentManager(this).getComponent(MainConfig.class).getLicenseKey();
        String serverName = ComponentManager.getComponentManager(this).getComponent(MainConfig.class).getServerName();

        SentinelClient client = new SentinelClient(
                "http://193.31.31.184:25206/api/v1",
                "ht24ki1c4c6iivkm8ppgo9dcm4",
                null);

        boolean authenticated = false;

        try {
            client.getLicenseController().auth(
                    licenseKey, "Casino", null, null, serverName, this.getServer().getIp().toString());
            authenticated = true;
        } catch (ApiException e) {
            Bukkit.getLogger().warning("Failed to verify license: " + e.getResponse().getMessage());
        } catch (Exception e) {
            Bukkit.getLogger().warning("An unexpected error occurred: " + e.getMessage());
        }

        return authenticated;
    }
}
