package io.github.beez131github.jsonite;

import io.github.beez131github.jsonite.block.ModBlocks;
import io.github.beez131github.jsonite.item.ModFoodComponents;
import io.github.beez131github.jsonite.item.ModItems;
import io.github.beez131github.jsonite.item.ModWeapons;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


public class Jsonite implements ModInitializer {
	public static final String MOD_ID = "jsonite";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String NAMESPACE = "jsonite";
	public static final net.minecraft.util.Identifier VERIFICATION_CHANNEL = net.minecraft.util.Identifier.of(NAMESPACE, "verification");

	// A set to hold all mod IDs detected in the jsonite folder
	public static final Set<String> MOD_IDS = new HashSet<>();

	@Override
	public void onInitialize() {
		// Initialize the mod by detecting all mod IDs
		System.out.println("Jsonite Mod is initializing...");
		loadModIds();
		// Debug: Print all detected mod IDs
		MOD_IDS.forEach(modid -> System.out.println("Detected mod ID: " + modid));
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModFoodComponents.registerModFoods();
		ModWeapons.registerModWeapons();
		scanPacks();
	}
	/**
	 * Loads all mod IDs from the `jsonite/` folder.
	 */
	private void loadModIds() {
		Path basePath = Paths.get("resourcepacks/");
		try {
			if (!Files.exists(basePath)) {
				// Create the folder if it doesn't exist
				Files.createDirectories(basePath);
				System.out.println("Created resourcepacks directory: " + basePath.toAbsolutePath());
				return;
			}

			// Traverse only one level below `resourcepacks/`
			Files.list(basePath)
				.filter(Files::isDirectory) // Only process directories
				.forEach(modidPath -> MOD_IDS.add(modidPath.getFileName().toString()));

		} catch (IOException e) {
			System.err.println("Error while loading mod IDs: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public static void scanPacks() {
		Path resourcePacksPath = Paths.get("resourcepacks");

		try (Stream<Path> modFolders = Files.list(resourcePacksPath)) {
			modFolders.filter(Files::isDirectory).forEach(packPath -> {
				String packId = packPath.getFileName().toString();
				Path dataPath = packPath.resolve("data").resolve(packId);

				boolean hasItems = Files.exists(dataPath.resolve("items"));
				boolean hasBlocks = Files.exists(dataPath.resolve("blocks"));
				boolean hasWeapons = Files.exists(dataPath.resolve("weapons"));
				boolean hasFoods = Files.exists(dataPath.resolve("foods"));

				if (hasItems || hasBlocks || hasWeapons || hasFoods) {
                    Jsonite.LOGGER.info("Found Jsonite content in pack: {}", packId);
					if (hasItems) Jsonite.LOGGER.info("- Contains custom items");
					if (hasBlocks) Jsonite.LOGGER.info("- Contains custom blocks");
					if (hasWeapons) Jsonite.LOGGER.info("- Contains custom weapons");
					if (hasFoods) Jsonite.LOGGER.info("- Contains custom foods");
				}
			});
		} catch (IOException e) {
			Jsonite.LOGGER.error("Failed to scan resource packs", e);
		}
	}



}
