package fr.rushland.database.injector;

import com.google.inject.AbstractModule;
import fr.rushland.database.Database;
import fr.rushland.database.Manager;
import fr.rushland.utils.DatabaseUtils;

import java.util.concurrent.locks.ReentrantLock;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Manager.class).to(Database.class).asEagerSingleton();
        bind(ReentrantLock.class).asEagerSingleton();
        bind(DatabaseUtils.class).asEagerSingleton();
    }
}
