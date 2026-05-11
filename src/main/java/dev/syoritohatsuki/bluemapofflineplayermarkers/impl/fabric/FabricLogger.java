package dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric;

import com.technicjelle.bluemapofflineplayermarkers.common.Logger;

public class FabricLogger implements Logger {
    @Override
    public void error(String message) {
        BluemapOfflinePlayerMarkers.LOGGER.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        BluemapOfflinePlayerMarkers.LOGGER.error(message, throwable);
    }

    @Override
    public void warning(String message) {
        BluemapOfflinePlayerMarkers.LOGGER.warn(message);
    }

    @Override
    public void info(String message) {
        BluemapOfflinePlayerMarkers.LOGGER.info(message);
    }

    @Override
    public void debug(String message) {
        BluemapOfflinePlayerMarkers.LOGGER.debug(message);
    }

    @Override
    public void trace(String message) {
        BluemapOfflinePlayerMarkers.LOGGER.trace(message);
    }
}
