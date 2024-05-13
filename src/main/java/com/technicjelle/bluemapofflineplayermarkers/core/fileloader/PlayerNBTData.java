package com.technicjelle.bluemapofflineplayermarkers.core.fileloader;

import com.flowpowered.math.vector.Vector3d;
import com.technicjelle.bluemapofflineplayermarkers.common.PlayerData;
import com.technicjelle.bluemapofflineplayermarkers.core.GameMode;
import de.bluecolored.bluenbt.NBTName;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlayerNBTData implements PlayerData {
    @NBTName("playerGameType")
    private int gameMode;

    @NBTName("Pos")
    private double[] position;

    @NBTName("WorldUUIDLeast")
    private long worldUUIDLeast;

    @NBTName("WorldUUIDMost")
    private long worldUUIDMost;

    @NBTName("Dimension")
    private Object dimension;

    public @Nullable GameMode getGameMode() {
        return GameMode.getByValue(gameMode);
    }

    public @Nullable Vector3d getPosition() {
        if (position.length != 3) return null; // Position is broken

        return new Vector3d(position[0], position[1], position[2]);
    }

    @Nullable
    public Optional<UUID> getWorldUUID() {
        // Unused in Fabric
        return Optional.empty();
    }

    @Nullable
    public Optional<String> getDimension() {
        return Optional.of(dimension.toString());
    }
}