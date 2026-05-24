package net.craftsupport.anticrasher.fabric.listener;

import net.craftsupport.anticrasher.fabric.AntiCrasher;
import net.craftsupport.anticrasher.fabric.service.ServiceManager;
import net.craftsupport.anticrasher.fabric.user.FabricUser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.util.UUID;

public class LifecycleEvents {
    public static final LifecycleEvents instance = new LifecycleEvents();

    public void listen() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            AntiCrasher.server = server;
        });

        // breaks in SERVER_STARTING in 1.21.7/8
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            AntiCrasher.instance.setConsoleUser(new FabricUser(UUID.randomUUID(), server.getCommandSource())); // cannot do in onInitialize
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ServiceManager.onDisable();
        });
    }
}
