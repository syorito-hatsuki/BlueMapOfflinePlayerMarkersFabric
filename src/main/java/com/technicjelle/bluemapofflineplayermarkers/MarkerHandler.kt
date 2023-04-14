package com.technicjelle.bluemapofflineplayermarkers

import com.technicjelle.bluemapofflineplayermarkers.Main.logger
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.BlueMapMap
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameMode

object MarkerHandler {

    /**
     * Adds a player marker to the map.
     *
     * @param player   The player to add the marker for.
     * @param location The location to put the marker at.
     * @param gameMode The game mode of the player.
     */
    fun add(
        player: ServerPlayerEntity,
        location: BlockPos = player.blockPos,
        gameMode: GameMode = player.interactionManager.gameMode
    ) {

    }

    /**
     * For when BlueMap doesn't have an icon for this player yet, so we need to make it create one.
     * @return Whether the player head was created successfully. <br>
     * If <code>true</code>, the player head was created successfully.<br>
     * If <code>false</code>, the player head was not created successfully and the fallback icon should be used instead.
     */
    fun createPlayerHead(player: ServerPlayerEntity, assetName: String, api: BlueMapAPI, map: BlueMapMap): Boolean {

    }

    /**
     * Removes a player marker from the map.
     *
     * @param player The player to remove the marker for.
     */
    fun remove(player: ServerPlayerEntity) {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            logger.warning("Tried to remove a marker, but BlueMap wasn't loaded!")
            return
        }
        val api = optionalApi.get()

        // remove all markers with the players uuid
        for (map in api.maps) {
            map.markerSets[ConfigManager.read().markerSetName]?.remove(player.uuidAsString)
        }

        logger.info("Marker for " + player.name + " removed")
    }

    /**
     * Load in markers of all offline players by going through the playerdata NBT
     */
    fun loadOfflineMarkers() {

    }
}