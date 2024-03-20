package xyz.zeppelin.casino.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.zeppelin.casino.component.PluginComponent;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseConfig implements PluginComponent {

    protected final YamlConfiguration configuration = new YamlConfiguration();
    protected final File file;
    protected final Logger logger;
    private final String defaultName;

    public BaseConfig(File file, String defaultName, Logger logger) {
        this.file = file;
        this.defaultName = defaultName;
        this.logger = logger;
    }

    @Override
    public void onEnable() {
        if (file.exists()) {
            load();
        } else {
            loadDefaults();
            save();
        }
    }

    private void load() {
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            logger.log(Level.SEVERE, "Could not load messages configuration", e);
        }
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save messages configuration", e);
        }
    }

    private void loadDefaults() {
        try (InputStream inputStream = getClass().getResourceAsStream(defaultName)) {
            if (inputStream == null) throw new FileNotFoundException("Default config " + defaultName + " not found");
            configuration.load(new InputStreamReader(inputStream));
        } catch (IOException | InvalidConfigurationException e) {
            logger.log(Level.SEVERE, "Could not load default messages configuration", e);
        }
    }

    public void reload() {
        load();
    }
}
