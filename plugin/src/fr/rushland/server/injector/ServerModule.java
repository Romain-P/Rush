package fr.rushland.server.injector;

import com.google.inject.AbstractModule;
import fr.rushland.server.Server;
import fr.rushland.server.ServerStuff;

public class ServerModule extends AbstractModule {
    protected void configure() {
        bind(Server.class).asEagerSingleton();
        bind(ServerStuff.class).asEagerSingleton();
    }
}
