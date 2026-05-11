package com.technicjelle.bluemapofflineplayermarkers.core.fileloader;

import com.technicjelle.bluemapofflineplayermarkers.core.Player;
import com.technicjelle.bluemapofflineplayermarkers.core.Singletons;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NBTReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class FileMarkerLoader {
	private static final BlueNBT nbt = new BlueNBT();

	/// Call from BlueMap's onEnable (which you should have registered in your mod/plugin's onEnable/onStart/etc)
	public static void loadOfflineMarkers(BlueMapAPI api) {
		Path playerDataFolder = Singletons.getServer().getPlayerDataFolder();

		//Return if playerdata is missing for some reason.
		if (!Files.exists(playerDataFolder) || !Files.isDirectory(playerDataFolder)) {
			Singletons.getLogger().error("Playerdata folder not found, skipping loading of offline markers from storage");
			return;
		}

		try (Stream<Path> playerDataFiles = Files.list(playerDataFolder)) {
			playerDataFiles.filter(p -> p.toString().endsWith(".dat")).forEach(p -> loadOfflineMarker(p, api));
		} catch (IOException e) {
			Singletons.getLogger().error("Failed to stream playerdata", e);
		}
	}

	private static void loadOfflineMarker(Path playerDataFile, BlueMapAPI api) {
		final String fileName = playerDataFile.getFileName().toString();
		Singletons.getLogger().trace("Loading playerdata file: " + fileName);

		final String uuidString = fileName.replace(".dat", "");
		final UUID playerUUID;
		try {
			playerUUID = UUID.fromString(uuidString);
		} catch (IllegalArgumentException e) {
			Singletons.getLogger().warning("Invalid playerdata filename: " + fileName + ", skipping");
			return;
		}

		if (playerDataFile.toFile().length() == 0) {
			Singletons.getLogger().warning("Playerdata file " + fileName + " is empty, skipping");
			return;
		}

		if (playerUUID.getLeastSignificantBits() == 0 && playerUUID.getMostSignificantBits() == 0) {
			Singletons.getLogger().warning("Invalid player UUID: " + fileName + ", skipping");
			return;
		}

		if (Singletons.getServer().isPlayerOnline(playerUUID)) return; // don't add markers for online players

		if (Singletons.getConfig().checkPlayerLastPlayed(playerUUID)) return; // don't add markers for players that have logged off too long ago

		try (GZIPInputStream in = new GZIPInputStream(Files.newInputStream(playerDataFile))) {
			NBTReader reader = new NBTReader(in);
			NBTPlayerData NBTPlayerData = nbt.read(reader, NBTPlayerData.class);

			Player player = new Player(playerUUID, NBTPlayerData);
			Singletons.getMarkerHandler().add(player, api);
		} catch (IOException e) {
			Singletons.getLogger().error("Failed to read playerdata file " + fileName, e);
		}
	}
}