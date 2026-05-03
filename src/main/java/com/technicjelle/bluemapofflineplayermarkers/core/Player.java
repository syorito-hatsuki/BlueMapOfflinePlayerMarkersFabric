package com.technicjelle.bluemapofflineplayermarkers.core;

import com.technicjelle.bluemapofflineplayermarkers.common.PlayerData;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class Player {
	private final UUID playerUUID;
	private final String playerName;
	/**
	 * The last time the player was online.
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // This lint suggests that Optional shouldn't be used as class member variables; only function return values. In this case, it makes complete sense to do.
	private final Optional<Instant> lastPlayed;
	private final PlayerData playerData;

	public Player(UUID uuid, PlayerData playerData) {
		this.playerUUID = uuid;
		this.playerName = Singletons.getServer().getPlayerName(uuid);
		this.lastPlayed = Singletons.getServer().getPlayerLastPlayed(uuid);
		this.playerData = playerData;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Optional<Instant> getLastPlayed() {
		return lastPlayed;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}
}
