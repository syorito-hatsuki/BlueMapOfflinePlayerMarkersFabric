package com.technicjelle.bluemapofflineplayermarkers.impl.fabric;

import com.google.gson.GsonBuilder;
import com.technicjelle.bluemapofflineplayermarkers.common.Config;
import com.technicjelle.bluemapofflineplayermarkers.core.GameMode;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FabricConfig implements Config {
    private String markerSetName = "Offline Players";
    private boolean toggleable = true;
    private boolean defaultHidden = false;
    private long expireTimeInHours = 0;
    private List<String> hiddenGameModes = List.of(GameMode.SPECTATOR.name());
    private boolean hideBannedPlayers = true;

    public FabricConfig() {
    }

    public void createAndReadConfig() {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var configFolderPath = FabricLoader.getInstance().getConfigDir();
            var configFile = new File(configFolderPath.toFile(), "bluemapofflineplayermarkers.json");
            if (!configFile.exists()) {
                configFile.createNewFile();
                Files.writeString(configFile.toPath(), gson.toJson(this));
            }

            var json = gson.fromJson(new FileReader(configFile), FabricConfig.class);

            markerSetName = json.markerSetName;
            toggleable = json.toggleable;
            defaultHidden = json.defaultHidden;
            expireTimeInHours = json.expireTimeInHours;
            hiddenGameModes = json.hiddenGameModes;
            hideBannedPlayers = json.hideBannedPlayers;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMarkerSetName() {
        return markerSetName;
    }

    @Override
    public boolean isToggleable() {
        return toggleable;
    }

    @Override
    public boolean isDefaultHidden() {
        return defaultHidden;
    }

    @Override
    public long getExpireTimeInHours() {
        return expireTimeInHours;
    }

    @Override
    public List<GameMode> getHiddenGameModes() {
        return hiddenGameModes.stream().map(GameMode::getById).toList();
    }

    @Override
    public boolean hideBannedPlayers() {
        return hideBannedPlayers;
    }
}