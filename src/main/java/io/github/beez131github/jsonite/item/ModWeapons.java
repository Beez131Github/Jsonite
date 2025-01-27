package io.github.beez131github.jsonite.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.beez131github.jsonite.Jsonite;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;


public class ModWeapons {
	private static final Map<String, Item> REGISTERED_WEAPONS = new HashMap<>();

	private static Item createWeaponFromJson(JsonObject json, String weaponType) {
		Item.Settings settings = new Item.Settings();

		if (json.has("fireproof") && json.get("fireproof").getAsBoolean()) {
			settings.fireproof();
		}

		return switch (weaponType.toLowerCase()) {
			case "sword" -> new SwordItem(
				getTierFromJson(json),
				json.get("attackDamage").getAsInt(),
				json.get("attackSpeed").getAsFloat(),
				settings
			);
			case "bow" -> new BowItem(settings.maxDamage(json.get("durability").getAsInt()));
			case "crossbow" -> new CrossbowItem(settings.maxDamage(json.get("durability").getAsInt()));
			case "arrow" -> json.has("tipped") && json.get("tipped").getAsBoolean()
				? new TippedArrowItem(settings)
				: new ArrowItem(settings);
			default -> throw new IllegalArgumentException("Unknown weapon type: " + weaponType);
		};
	}

	private static ToolMaterial getTierFromJson(JsonObject json) {
		if (!json.has("tier")) {
			throw new IllegalArgumentException("Weapon requires a tier!");
		}

		String tierName = json.get("tier").getAsString().toLowerCase();
		return switch (tierName) {
			case "wood" -> ToolMaterial.WOOD;
			case "stone" -> ToolMaterial.STONE;
			case "iron" -> ToolMaterial.IRON;
			case "gold" -> ToolMaterial.GOLD;
			case "diamond" -> ToolMaterial.DIAMOND;
			case "netherite" -> ToolMaterial.NETHERITE;
			default -> throw new IllegalArgumentException("Unknown tier: " + tierName);
		};
	}

	private static void registerWeaponFromJson(Path jsonPath, String modId) {
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject json = JsonParser.parseString(jsonContent).getAsJsonObject();

			if (!json.has("name") || !json.has("type")) {
				Jsonite.LOGGER.error("Weapon JSON must contain 'name' and 'type' fields: {}", jsonPath);
				return;
			}

			String name = json.get("name").getAsString();
			String type = json.get("type").getAsString();
			Identifier id = Identifier.of(modId, name);

			// Create registry key
			RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

			// Create settings with registry key
			Item.Settings settings = new Item.Settings().registryKey(key);

			// Update createWeaponFromJson to accept settings
			Item weapon = createWeaponFromJson(json, type, settings);

			Registry.register(Registries.ITEM, id, weapon);
			REGISTERED_WEAPONS.put(modId + ":" + name, weapon);

			Jsonite.LOGGER.info("Registered weapon: {}", id);
		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to register weapon from JSON: {}", jsonPath, e);
		}
	}

	// Update the createWeaponFromJson method signature
	private static Item createWeaponFromJson(JsonObject json, String weaponType, Item.Settings settings) {
		if (json.has("fireproof") && json.get("fireproof").getAsBoolean()) {
			settings.fireproof();
		}

		return switch (weaponType.toLowerCase()) {
			case "sword" -> new SwordItem(
					getTierFromJson(json),
					json.get("attackDamage").getAsInt(),
					json.get("attackSpeed").getAsFloat(),
					settings
			);
			case "bow" -> new BowItem(settings.maxDamage(json.get("durability").getAsInt()));
			case "crossbow" -> new CrossbowItem(settings.maxDamage(json.get("durability").getAsInt()));
			case "arrow" -> json.has("tipped") && json.get("tipped").getAsBoolean()
					? new TippedArrowItem(settings)
					: new ArrowItem(settings);
			default -> throw new IllegalArgumentException("Unknown weapon type: " + weaponType);
		};
	}


	public static void registerModWeapons(String packId) {
		Path resourcePacksPath = Paths.get("resourcepacks");

		try (Stream<Path> modFolders = Files.list(resourcePacksPath)) {
			modFolders.filter(Files::isDirectory).forEach(modFolder -> {
				String modId = modFolder.getFileName().toString();
				Path weaponsFolder = modFolder.resolve("data").resolve(modId).resolve("weapons");

				if (!Files.exists(weaponsFolder)) {
					return;
				}

				try (Stream<Path> paths = Files.walk(weaponsFolder, 1)) {
					paths.filter(Files::isRegularFile)
						.filter(path -> path.toString().endsWith(".json"))
						.forEach(path -> registerWeaponFromJson(path, modId));
				} catch (IOException e) {
					Jsonite.LOGGER.error("Failed to load weapons for mod ID: {}", modId, e);
				}
			});
		} catch (IOException e) {
			Jsonite.LOGGER.error("Failed to traverse resourcepacks folder", e);
		}
	}

	public static void clearRegisteredWeapons() {
		REGISTERED_WEAPONS.clear();
		Jsonite.LOGGER.info("Cleared all registered weapons");
	}
}