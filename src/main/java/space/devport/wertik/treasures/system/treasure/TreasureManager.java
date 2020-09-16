package space.devport.wertik.treasures.system.treasure;

import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.GsonHelper;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TreasureManager {

    private final TreasurePlugin plugin;

    private final GsonHelper gsonHelper;

    private final Map<UUID, Treasure> loadedTreasures = new HashMap<>();

    public TreasureManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.gsonHelper = plugin.getGsonHelper();
    }

    public void load() {
        this.loadedTreasures.clear();

        Map<UUID, Treasure> treasures = gsonHelper.load(plugin.getDataFolder() + "/data.json", new TypeToken<Map<UUID, Treasure>>() {
        }.getType());

        if (treasures == null) treasures = new HashMap<>();

        this.loadedTreasures.putAll(treasures);

        plugin.getConsoleOutput().info("Loaded " + this.loadedTreasures.size() + " treasure(s)...");
    }

    public void save() {
        gsonHelper.save(this.loadedTreasures, plugin.getDataFolder() + "/data.json");
        plugin.getConsoleOutput().info("Saved " + this.loadedTreasures.size() + " treasure(s)...");
    }

    public Treasure createTreasure(Location location) {
        Treasure treasure = new Treasure(location);
        this.loadedTreasures.put(treasure.getUniqueID(), treasure);
        plugin.getConsoleOutput().debug("Created treasure " + treasure.getUniqueID());
        return treasure;
    }

    public Treasure createTreasure(Location location, PlacementTool tool) {
        Treasure treasure = createTreasure(location);
        treasure.withTool(tool);
        plugin.getConsoleOutput().debug("...with template " + treasure.getTool().getName());
        return treasure;
    }

    public Treasure getTreasure(Location location) {
        for (Treasure treasure : this.loadedTreasures.values()) {
            if (treasure.getLocation().equals(location)) {
                return treasure;
            }
        }
        return null;
    }

    public Treasure getTreasure(UUID uniqueID) {
        return this.loadedTreasures.get(uniqueID);
    }

    public boolean deleteTreasure(UUID uniqueID) {
        return deleteTreasure(getTreasure(uniqueID));
    }

    public boolean deleteTreasure(Treasure treasure) {
        if (treasure == null) return false;

        this.loadedTreasures.remove(treasure.getUniqueID());
        plugin.getConsoleOutput().debug("Removed treasure " + treasure.getUniqueID());
        plugin.getUserManager().deleteAllReferences(treasure.getUniqueID());
        return true;
    }

    public Map<UUID, Treasure> getLoadedTreasures() {
        return Collections.unmodifiableMap(loadedTreasures);
    }
}