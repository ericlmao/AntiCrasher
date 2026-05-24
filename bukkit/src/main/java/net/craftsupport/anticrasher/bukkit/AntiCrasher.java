package net.craftsupport.anticrasher.bukkit;

import com.github.puregero.multilib.MultiLib;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.bstats.bukkit.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.craftsupport.anticrasher.api.AntiCrasherAPI;
import net.craftsupport.anticrasher.api.Platform;
import net.craftsupport.anticrasher.api.user.User;
import net.craftsupport.anticrasher.api.util.Version;
import net.craftsupport.anticrasher.bukkit.alert.BukkitAlertManager;
import net.craftsupport.anticrasher.common.manager.CheckManager;
import net.craftsupport.anticrasher.common.util.ACLogger;
import net.craftsupport.anticrasher.bukkit.api.BukkitAntiCrasherAPI;
import net.craftsupport.anticrasher.bukkit.command.BukkitCommandHandler;
import net.craftsupport.anticrasher.bukkit.listener.PlayerEvents;
import net.craftsupport.anticrasher.bukkit.user.BukkitUser;
import net.craftsupport.anticrasher.common.config.Config;
import net.craftsupport.anticrasher.common.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.UUID;

public class AntiCrasher extends JavaPlugin implements Platform {

    public static AntiCrasher instance;
    private User consoleUser;

    public AntiCrasher() {
        instance = this;
    }

    public static AntiCrasher getInstance() {
        return instance;
    }

    @Override
    public Path getConfigDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(true)
                .kickOnPacketException(true);

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        ACLogger.info("Enabling AntiCrasher...");
        PacketEvents.getAPI().init();
        AntiCrasherAPI.setInstance(new BukkitAntiCrasherAPI());

        ACLogger.info("Initialising Metrics.");
        new Metrics(this, 20218);

        Config.i();
        CheckManager.getInstance().initialise();
        BukkitAlertManager.instance.initialise();
        BukkitCommandHandler.getInstance().initialise();
        UpdateChecker.getInstance().check();
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        this.consoleUser = new BukkitUser(UUID.randomUUID(), Bukkit.getConsoleSender());

        ACLogger.info("AntiCrasher enabled with %s checks enabled.".formatted(CheckManager.getInstance().checks.size()));
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();

        ACLogger.info("AntiCrasher has disabled.");
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    @Override
    public Version getCurrentVersion() {
        return Version.fromString(getDescription().getVersion());
    }

    @Override
    public void runLater(Runnable runnable, long delay) {
        MultiLib.getGlobalRegionScheduler().runDelayed(this, (task) -> runnable.run(), delay);
    }

    @Override
    public User getConsoleUser() {
        return consoleUser;
    }

    @Override
    public String getPlatformType() {
        return "plugin";
    }
}
