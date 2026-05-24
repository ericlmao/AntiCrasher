package net.skullian.anticrasher.velocity.service;

import lombok.experimental.UtilityClass;
import net.craftsupport.anticrasher.common.config.Config;
import net.craftsupport.anticrasher.common.manager.CheckManager;
import net.craftsupport.anticrasher.common.update.UpdateChecker;
import net.craftsupport.anticrasher.common.util.ACLogger;
import net.skullian.anticrasher.velocity.AntiCrasher;
import net.skullian.anticrasher.velocity.alert.VelocityAlertManager;
import net.skullian.anticrasher.velocity.command.VelocityCommandHandler;
import net.skullian.anticrasher.velocity.listener.PlayerEvents;

@UtilityClass
public class ServiceManager {

    public void onEnable() {
        ACLogger.info("Enabling AntiCrasher...");

        Config.i();
        CheckManager.getInstance().initialise();
        VelocityAlertManager.instance.initialise();
        VelocityCommandHandler.instance.initialise();
        AntiCrasher.getInstance().server.getEventManager().register(AntiCrasher.getInstance(), new PlayerEvents());
        UpdateChecker.getInstance().check();

        ACLogger.info("AntiCrasher enabled with %s checks enabled.".formatted(CheckManager.getInstance().checks.size()));
    }

    public void onDisable() {
        ACLogger.info("Closed all services.");
        ACLogger.info("AntiCrasher has disabled.");
    }
}
