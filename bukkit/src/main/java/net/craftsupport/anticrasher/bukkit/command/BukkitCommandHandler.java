package net.craftsupport.anticrasher.bukkit.command;

import net.craftsupport.anticrasher.api.AntiCrasherAPI;
import net.craftsupport.anticrasher.api.user.User;
import net.craftsupport.anticrasher.common.command.impl.ReloadCommand;
import net.craftsupport.anticrasher.common.util.ACLogger;
import net.craftsupport.anticrasher.bukkit.AntiCrasher;
import net.craftsupport.anticrasher.bukkit.user.BukkitUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.Objects;
import java.util.UUID;

public class BukkitCommandHandler {
    public static final BukkitCommandHandler instance = new BukkitCommandHandler();

    private LegacyPaperCommandManager<User> manager;
    private AnnotationParser<User> annotationParser;

    public static BukkitCommandHandler getInstance() {
        return instance;
    }

    public LegacyPaperCommandManager<User> getManager() {
        return manager;
    }

    public AnnotationParser<User> getAnnotationParser() {
        return annotationParser;
    }

    public void initialise() {
        SenderMapper<CommandSender, User> senderMapper = SenderMapper.create(
                commandSender -> {
                    if (commandSender instanceof Player player) {
                        return Objects.requireNonNull(AntiCrasherAPI.getInstance().getUserManager().getOrCreate(
                                player.getUniqueId(),
                                player
                        ));
                    }

                    return new BukkitUser(UUID.randomUUID(), commandSender);
                },
                sender -> (CommandSender) sender.getSource()
        );

        this.manager = new LegacyPaperCommandManager<>(
                AntiCrasher.getInstance(),
                ExecutionCoordinator.asyncCoordinator(),
                senderMapper
        );
        this.annotationParser = new AnnotationParser<>(
                this.manager,
                User.class,
                params -> SimpleCommandMeta.empty()
        );

        registerSubCommands();

        ACLogger.info("Registered commands.");
    }

    private void registerSubCommands() {
        annotationParser.parse(
                new ReloadCommand()
        );
    }
}
