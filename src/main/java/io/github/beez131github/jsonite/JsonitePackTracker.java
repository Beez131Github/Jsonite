package io.github.beez131github.jsonite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JsonitePackTracker {
    private static final Map<String, Boolean> PACKS_WITH_JSONITE = new HashMap<>();

    public static boolean hasJsoniteContent() {
        Path resourcePacksPath = Paths.get("resourcepacks");
        try (Stream<Path> modFolders = Files.list(resourcePacksPath)) {
            return modFolders.filter(Files::isDirectory).anyMatch(packPath -> {
                String packId = packPath.getFileName().toString();
                Path dataPath = packPath.resolve("data").resolve(packId);

                boolean hasItems = Files.exists(dataPath.resolve("items"));
                boolean hasBlocks = Files.exists(dataPath.resolve("blocks"));
                boolean hasWeapons = Files.exists(dataPath.resolve("weapons"));
                boolean hasFoods = Files.exists(dataPath.resolve("foods"));

                boolean hasContent = hasItems || hasBlocks || hasWeapons || hasFoods;

                if (hasContent) {
                    Jsonite.LOGGER.info("Found Jsonite content in pack: {}", packId);
                    if (hasItems) Jsonite.LOGGER.info("- Contains custom items");
                    if (hasBlocks) Jsonite.LOGGER.info("- Contains custom blocks");
                    if (hasWeapons) Jsonite.LOGGER.info("- Contains custom weapons");
                    if (hasFoods) Jsonite.LOGGER.info("- Contains custom foods");
                }
                return hasContent;
            });
        } catch (IOException e) {
            Jsonite.LOGGER.error("Failed to scan resource packs", e);
            return false;
        }
    }
}

