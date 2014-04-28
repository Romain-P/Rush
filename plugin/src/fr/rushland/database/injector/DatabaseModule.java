package fr.rushland.database.injector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import fr.rushland.database.Database;
import fr.rushland.database.Manager;
import fr.rushland.database.data.PlayerManager;
import fr.rushland.database.data.ServerManager;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).asEagerSingleton();
        bind(ReentrantLock.class).asEagerSingleton();
        Multibinder<Manager> binder = Multibinder.newSetBinder(binder(), Manager.class);
        binder.addBinding().toInstance(new PlayerManager());
        binder.addBinding().toInstance(new ServerManager());
    }
}
