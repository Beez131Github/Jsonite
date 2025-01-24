package io.github.beez131github.jsonite.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.beez131github.jsonite.Jsonite;
import io.github.beez131github.jsonite.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

public class ModItems {

	/**
	 * Parse Item.Settings from a JSON file.
	 */
	private static Item.Settings getItemSettingsFromJson(Path jsonPath) {
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

			Item.Settings settings = new Item.Settings();
			if (jsonObject.has("maxCount")) {
				settings.maxCount(jsonObject.get("maxCount").getAsInt());
				Jsonite.LOGGER.info("Set maxCount status");
			}
			if (jsonObject.has("fireproof") && jsonObject.get("fireproof").getAsBoolean()) {
				settings.fireproof();
				Jsonite.LOGGER.info("Set item fireproof status");
			}
			// Parse rarity
			if (jsonObject.has("rarity")) {
				String rarityString = jsonObject.get("rarity").getAsString().toLowerCase();
				Rarity rarity = switch (rarityString) {
					case "common" -> Rarity.COMMON;
					case "uncommon" -> Rarity.UNCOMMON;
					case "rare" -> Rarity.RARE;
					case "epic" -> Rarity.EPIC;
					default -> Rarity.COMMON; // Default to COMMON
				};
				Jsonite.LOGGER.info("Set rarity to {}", rarity);
				settings.rarity(rarity); // Assign parsed rarity to settings
			}

			return settings;

		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to create Item.Settings from JSON: {}", jsonPath, e);
			return new Item.Settings();
		}
	}


	/**
	 * Register an item from a JSON file.
	 */
	private static void registerItemFromJson(Path jsonPath, String modId) {
		Jsonite.LOGGER.info("Loading JSON file: {}", jsonPath);
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

			// Validate and extract 'name' field
			if (!jsonObject.has("name") || jsonObject.get("name").getAsString().isEmpty()) {
				Jsonite.LOGGER.error("The 'name' field is missing or empty in JSON file: {}", jsonPath);
				return;
			}

			String name = jsonObject.get("name").getAsString();
			Identifier id = Identifier.of(modId, name); // Use the provided modId here

			// Create a RegistryKey for the item
			RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

			// Add RegistryKey to Item.Settings
			Item.Settings settings = getItemSettingsFromJson(jsonPath).key(key);

			// Register the item
			Item item = new Item(settings);
			Registry.register(Registries.ITEM, id, item);

			Jsonite.LOGGER.info("Successfully registered item: {}", name);

		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to register item from JSON: {}", jsonPath, e);
		}
	}


	/**
	 * Register all mod items from the `config/jsonite` folder.
	 */
	public static void registerModItems() {
		Jsonite.LOGGER.info("Registering Mod Items for " + Jsonite.MOD_ID);

		// Iterate over each detected mod ID
		Set<String> modIds = Jsonite.MOD_IDS;
		for (String modId : modIds) {
			Path itemsFolder = Paths.get("config/jsonite", modId, "items");

			// Skip if `items` subdirectory doesn't exist
			if (!Files.exists(itemsFolder)) {
				Jsonite.LOGGER.warn("No items folder found for mod ID: {}", modId);
				continue;
			}

			// Process JSON files in the `items` subdirectory
			try (Stream<Path> paths = Files.walk(itemsFolder, 1)) {
				paths.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".json"))
					.forEach(path -> registerItemFromJson(path, modId)); // Pass modId here
			} catch (IOException e) {
				Jsonite.LOGGER.error("Failed to load items for mod ID: {}", modId, e);
			}
		}
	}

}
