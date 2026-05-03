package com.technicjelle.bluemapofflineplayermarkers.common;

import com.flowpowered.math.vector.Vector3d;
import com.technicjelle.bluemapofflineplayermarkers.core.GameMode;

import java.util.Optional;
import java.util.UUID;

public interface PlayerData {
	GameMode getGameMode();

	Vector3d getPosition();

	Optional<UUID> getWorldUUID();

	/// For the NBT implementation, this is the `Dimension` key
	///
	/// For server implementations, I recommend making this return the platform's World object that feeds directly into <code>BlueMapAPI.getWorld</code>.<br>
	/// And then in <code>Server.getBlueMapWorldForPlayer</code>, check if the PlayerData instanceof YourPlayerData and put the getDimension() method into getWorld().
	///
	/// @see de.bluecolored.bluemap.api.BlueMapAPI#getWorld(Object)
	Object getDimension();
}