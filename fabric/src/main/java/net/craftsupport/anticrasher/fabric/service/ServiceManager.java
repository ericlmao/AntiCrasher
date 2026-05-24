package net.craftsupport.anticrasher.fabric.service;

import lombok.experimental.UtilityClass;
import net.craftsupport.anticrasher.common.config.Config;
import net.craftsupport.anticrasher.common.manager.CheckManager;
import net.craftsupport.anticrasher.common.update.UpdateChecker;
import net.craftsupport.anticrasher.common.util.ACLogger;
import net.craftsupport.anticrasher.fabric.alert.FabricAlertManager;
import net.craftsupport.anticrasher.fabric.command.FabricCommandHandler;
import net.craftsupport.anticrasher.fabric.listener.LifecycleEvents;
import net.craftsupport.anticrasher.fabric.listener.PlayerEvents;

@UtilityClass
public class ServiceManager {

    public void onEnable() {
        ACLogger.info("Enabling AntiCrasher...");

        Config.i();
        CheckManager.getInstance().initialise();
        FabricAlertManager.instance.initialise();
        FabricCommandHandler.getInstance().initialise();
        LifecycleEvents.instance.listen();
        PlayerEvents.instance.listen();
        UpdateChecker.getInstance().check();

        ACLogger.info("AntiCrasher enabled with %s checks enabled.".formatted(CheckManager.getInstance().checks.size()));
    }

    public void onDisable() {
        ACLogger.info("Closed all services.");
        ACLogger.info("AntiCrasher has disabled.");
    }
}
