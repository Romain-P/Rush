package fr.rushland.core.injector;

import com.google.inject.AbstractModule;
import fr.rushland.core.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultModule extends AbstractModule{
    private JavaPlugin plugin;

    public DefaultModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
        bind(Config.class).asEagerSingleton();
    }
}
