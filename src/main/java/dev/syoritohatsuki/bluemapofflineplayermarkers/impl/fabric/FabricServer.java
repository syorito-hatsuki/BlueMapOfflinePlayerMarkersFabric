package dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric;

import com.technicjelle.bluemapofflineplayermarkers.common.Server;
import com.technicjelle.bluemapofflineplayermarkers.core.Player;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric.util.BukkitData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class FabricServer implements Server {

    MinecraftServer server;
    BukkitData bukkitData;

    public FabricServer(MinecraftServer server) {
        this.server = server;
        this.bukkitData = new BukkitData(server.getWorldPath(LevelResource.PLAYER_DATA_DIR));
    }

    @Override
    public void startUp() {
        // Override with empty because Vanilla Fabric already has cache file
    }

    @Override
    public void shutDown() {
        // Override with empty because Vanilla Fabric already has cache file
    }

    @Override
    public boolean isPlayerOnline(UUID playerUUID) {
        return server.getPlayerList().getPlayer(playerUUID) != null;
    }

    @Override
    public Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getPlayerDataFolder() {
        return server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
    }

    @Override
    public Optional<Instant> getPlayerLastPlayed(UUID playerUUID) {
        var lastPlayed = bukkitData.getLastPlayed(playerUUID);

        if (lastPlayed != null && lastPlayed > 0) return Optional.of(Instant.ofEpochMilli(lastPlayed));

        return Optional.empty();
    }

    @Override
    public String getPlayerName(UUID playerUUID) {
        Optional<NameAndId> profile = server.services().nameToIdCache().get(playerUUID);
        if (profile.isPresent()) return profile.get().name();

        var lastKnownName = bukkitData.getLastKnownName(playerUUID);
        if (lastKnownName != null) return lastKnownName;

        try {
            return Server.nameFromMojangAPI(playerUUID);
        } catch (IOException e) {
            return playerUUID.toString();
        }
    }

    @Override
    public BlueMapWorld getBlueMapWorldForPlayer(BlueMapAPI api, Player player) {
        var dimension = player.getPlayerData().getDimension();

        if (dimension instanceof Integer dimensionInteger) {
            return api.getWorld(switch (dimensionInteger) {
                case 0 -> Level.OVERWORLD.identifier().toString();
                case 1 -> Level.NETHER.identifier().toString();
                case -1 -> Level.END.identifier().toString();
                default ->
                        throw new IllegalArgumentException("Invalid dimension integer: " + dimension + " for player: " + player.getPlayerUUID());
            }).orElseThrow(() -> new IllegalArgumentException("Could not find BlueMap world for dimension " + dimension + " from player: " + player.getPlayerUUID()));
        }

        if (dimension instanceof String) {
            return api.getWorld(dimension).orElseThrow(() -> new IllegalArgumentException("Could not find BlueMap world for dimension " + dimension + " from player: " + player.getPlayerUUID()));
        }

        throw new IllegalArgumentException("Invalid dimension type: " + dimension.getClass().getName() + " for player: " + player.getPlayerUUID());
    }

    @Override
    public boolean isPlayerBanned(UUID playerUUID) {
        return server.getPlayerList().getBans().toString().contains(playerUUID.toString());
    }
}
