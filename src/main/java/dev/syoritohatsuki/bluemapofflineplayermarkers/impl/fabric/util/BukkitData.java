package dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric.util;

import dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric.BluemapOfflinePlayerMarkers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public record BukkitData(Path playerDataFolder) {
    public Long getLastPlayed(UUID playerUUID) {
        CompoundTag nbt;

        try {
            nbt = NbtIo.readCompressed(playerDataFolder.resolve(playerUUID + ".dat"), NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            BluemapOfflinePlayerMarkers.LOGGER.warn("Can't read lastPlayed for player {} from {}", playerUUID, playerDataFolder);
            return null;
        }

        return nbt.getCompound("bukkit").flatMap(compoundTag -> compoundTag.getLong("lastPlayed")).orElse(null);
    }

    public String getLastKnownName(UUID playerUUID) {
        CompoundTag nbt;
        try {
            nbt = NbtIo.readCompressed(playerDataFolder.resolve(playerUUID + ".dat"), NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            BluemapOfflinePlayerMarkers.LOGGER.warn("Can't read lastKnowName for player {} from {}", playerUUID, playerDataFolder);
            return null;
        }
        return nbt.getCompound("bukkit").flatMap(compoundTag -> compoundTag.getString("lastKnownName")).orElse(null);
    }
}
