package fr.rushland.server.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.rushland.server.Server;
import fr.rushland.server.ServerStuff;
import fr.rushland.server.objects.*;

public class ServerModule extends AbstractModule {
    protected void configure() {
        bind(Server.class).asEagerSingleton();
        bind(ServerStuff.class).asEagerSingleton();
        bind(ClientServer.class).asEagerSingleton();
        bind(CustomStuff.class).asEagerSingleton();
    }
}
