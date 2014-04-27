package fr.rushland.database.injector;

import com.google.inject.AbstractModule;
import fr.rushland.database.Database;
import fr.rushland.database.Manager;
import fr.rushland.database.data.PlayerManager;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).asEagerSingleton();
        bind(Manager.class).to(PlayerManager.class).asEagerSingleton();
        bind(ReentrantLock.class).asEagerSingleton();
    }
}
