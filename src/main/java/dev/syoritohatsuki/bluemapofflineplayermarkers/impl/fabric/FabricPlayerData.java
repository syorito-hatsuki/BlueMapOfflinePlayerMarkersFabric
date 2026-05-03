package dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric;

import com.flowpowered.math.vector.Vector3d;
import com.technicjelle.bluemapofflineplayermarkers.common.PlayerData;
import com.technicjelle.bluemapofflineplayermarkers.core.GameMode;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class FabricPlayerData implements PlayerData {

    final ServerPlayer player;

    public FabricPlayerData(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(player.gameMode.getGameModeForPlayer().getId());
    }

    @Override
    public Vector3d getPosition() {
        var position = player.position();
        return Vector3d.from(position.x, position.y, position.z);
    }

    /**
     * Unused in Fabric. Bukkit legacy way to identify worlds.
     * @deprecated Use {@link #getDimension()} instead.
     */
    @Deprecated(since = "Bukkit")
    @Override
    public Optional<UUID> getWorldUUID() {
        return Optional.empty();
    }

    @Override
    public Object getDimension() {
        //noinspection resource
        return player.level()
                .dimensionTypeRegistration()
                .unwrapKey()
                .map(dimensionTypeResourceKey -> dimensionTypeResourceKey.identifier().toString())
                .orElse(null);
    }
}
