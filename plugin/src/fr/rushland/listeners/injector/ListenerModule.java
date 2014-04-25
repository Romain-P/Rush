package fr.rushland.listeners.injector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import fr.rushland.core.Config;
import fr.rushland.listeners.GamePlayerListener;
import fr.rushland.listeners.ServerPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerModule extends AbstractModule{
    @Override
    protected void configure() {
        Multibinder<Listener> binder = Multibinder.newSetBinder(binder(), Listener.class);
        binder.addBinding().toInstance(new GamePlayerListener());
        binder.addBinding().toInstance(new ServerPlayerListener());
    }
}
