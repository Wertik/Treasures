package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.utility.LocationUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TeleportSubCommand extends TreasureSubCommand {

    public TeleportSubCommand(TreasurePlugin plugin) {
        super(plugin, "teleport");
        setAliases("tp");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        CompletableFuture.runAsync(() -> {
            List<UUID> treasures = plugin.getTreasureManager().getTreasures(treasure -> treasure.getUniqueID().toString().startsWith(args[0]))
                    .stream().map(Treasure::getUniqueID)
                    .collect(Collectors.toList());

            if (treasures.size() > 1) {
                language.getPrefixed("Commands.Treasures.Teleport.Multiple")
                        .replace("%param%", args[0])
                        .send(sender);
                return;
            } else if (treasures.isEmpty()) {
                language.getPrefixed("Commands.Treasures.Teleport.Invalid-Treasure")
                        .replace("%param%", args[0])
                        .send(sender);
                return;
            }

            Treasure treasure = plugin.getTreasureManager().getTreasure(treasures.get(0));
            Location location = treasure.getLocation();

            Player player = (Player) sender;

            language.getPrefixed("Commands.Treasures.Teleporting")
                    .replace("%treasure%", treasure.getUniqueID())
                    .replace("%location%", LocationUtil.locationToString(treasure.getLocation()))
                    .send(player);

            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(location));
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% teleport <startOfUUID>";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Teleport to egg location.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}
