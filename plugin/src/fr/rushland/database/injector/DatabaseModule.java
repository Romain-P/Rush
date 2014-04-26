package fr.rushland.database.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.rushland.database.Database;
import fr.rushland.database.Manager;
import fr.rushland.utils.DataManager;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Database.class).asEagerSingleton();
        bind(Manager.class).to(DataManager.class).asEagerSingleton();
        bind(ReentrantLock.class).asEagerSingleton();
    }
}
