package xyz.zeppelin.casino;

import dev.demeng.sentinel.wrapper.SentinelClient;
import dev.demeng.sentinel.wrapper.exception.ApiException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.bstats.BstatsComponent;
import xyz.zeppelin.casino.command.CasinoCommand;
import xyz.zeppelin.casino.commandapi.CommandApiComponent;
import xyz.zeppelin.casino.common.Environment;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;

import java.util.List;

public class ZeppelinCasinoPlugin extends JavaPlugin {

    private final ComponentManager componentManager = ComponentManager.register(this, List.of(
            MainConfig.createDefault(this),
            MessagesConfig.createDefault(this),
            EconomyBridge.createDetected(this),
            new CommandApiComponent(this),
            new BstatsComponent(this),
            new CasinoCommand(this)
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

        if (authenticate()) {
            Bukkit.getLogger().info("License validated. Resuming boot!");
        } else {
            Bukkit.getLogger().warning("You do not have a valid license for Zeppelin Casino. The plugin will shut down now.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }


    @Override
    public void onDisable() {
        componentManager.disableComponents();
    }

    private boolean authenticate() {

        String licenseKey = ComponentManager.getComponentManager(this).getComponent(MainConfig.class).getLicenseKey();

        SentinelClient client = new SentinelClient(
                "http://193.31.31.184:25206/api/v1",
                "ht24ki1c4c6iivkm8ppgo9dcm4",
                null);

        boolean authenticated = false;

        try {
            client.getLicenseController().auth(
                    licenseKey, "Casino", null, null, this.getServer().getName(), this.getServer().getIp());
            authenticated = true;
        } catch (ApiException e) {
            Bukkit.getLogger().warning("Failed to verify license: " + e.getResponse().getMessage());
        } catch (Exception e) {
            Bukkit.getLogger().warning("An unexpected error occurred: " + e.getMessage());
        }

        return authenticated;
    }
}
