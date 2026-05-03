package mockery;

import com.technicjelle.bluemapofflineplayermarkers.core.Player;
import com.technicjelle.bluemapofflineplayermarkers.core.Singletons;
import com.technicjelle.bluemapofflineplayermarkers.core.markerhandler.MarkerHandler;
import de.bluecolored.bluemap.api.BlueMapAPI;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class MockMarkerHandler implements MarkerHandler {
	@Override
	public void add(Player player, BlueMapAPI __) {
		Singletons.getLogger().finer("UUID: " + player.getPlayerUUID());
		Singletons.getLogger().finer("Name: " + player.getPlayerName());
		Optional<Instant> oLastPlayed = player.getLastPlayed();
		if (oLastPlayed.isPresent()) {
			Singletons.getLogger().finer("Last Played: " + oLastPlayed.get().toEpochMilli());
		} else {
			Singletons.getLogger().finer("Last Played: not available");
		}
		Singletons.getLogger().finer("GameMode: " + player.getPlayerData().getGameMode());
		Singletons.getLogger().finer("Position: " + player.getPlayerData().getPosition());
		Singletons.getLogger().finer("Banned: " + Singletons.getServer().isPlayerBanned(player.getPlayerUUID()));

		Optional<UUID> worldUUID = player.getPlayerData().getWorldUUID();
		if (worldUUID.isEmpty())
			Singletons.getLogger().warning("World UUID: null");
		else
			Singletons.getLogger().finer("World UUID: " + worldUUID.get());
	}

	@Override
	public void remove(UUID __, BlueMapAPI ___) {
	}
}
