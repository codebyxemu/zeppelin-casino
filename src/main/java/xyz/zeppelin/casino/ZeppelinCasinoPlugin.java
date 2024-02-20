package xyz.zeppelin.casino;

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
    }


    @Override
    public void onDisable() {
        componentManager.disableComponents();
    }
}
