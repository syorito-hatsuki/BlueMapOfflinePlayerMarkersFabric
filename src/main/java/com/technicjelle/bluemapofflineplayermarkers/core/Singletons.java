package com.technicjelle.bluemapofflineplayermarkers.core;

import com.technicjelle.bluemapofflineplayermarkers.common.Config;
import com.technicjelle.bluemapofflineplayermarkers.common.Logger;
import com.technicjelle.bluemapofflineplayermarkers.common.Server;
import com.technicjelle.bluemapofflineplayermarkers.core.markerhandler.MarkerHandler;

public class Singletons {
	private static Server server;
	private static Logger logger;
	private static Config config;
	private static MarkerHandler markerHandler;

	public static void init(Server server, Logger logger, Config config, MarkerHandler markerHandler) {
		if (Singletons.server != null || Singletons.logger != null || Singletons.config != null || Singletons.markerHandler != null)
			throw new IllegalStateException("Singletons already initialized");

		Singletons.server = server;
		Singletons.logger = logger;
		Singletons.config = config;
		Singletons.markerHandler = markerHandler;
	}

	public static void cleanup() {
		server = null;
		logger = null;
		config = null;
		markerHandler = null;
		System.gc();
	}

	public static Server getServer() {
		return server;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Config getConfig() {
		return config;
	}

	public static MarkerHandler getMarkerHandler() {
		return markerHandler;
	}
}