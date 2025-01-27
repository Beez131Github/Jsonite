package io.github.beez131github.jsonite.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.beez131github.jsonite.Jsonite;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModBlocks {

	private static final Map<Identifier, Block> REGISTERED_BLOCKS = new HashMap<>();

	private static void registerBlockItem(String name, Block block, String modId) {
		Identifier id = Identifier.of(modId, name);
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
		Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);

		Registry.register(Registries.ITEM, id,
			new BlockItem(block, itemSettings));
	}

	private static Block registerBlock(String name, Block block, String modId, Identifier id) {
		registerBlockItem(name, block, modId);
		return Registry.register(Registries.BLOCK, id, block);
	}


	private static Settings getBlockSettingsFromJson(Path jsonPath, RegistryKey<Block> key) {
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

			Settings settings = Settings.create().registryKey(key);

			if (jsonObject.has("hardness")) {
				settings.hardness(jsonObject.get("hardness").getAsFloat());
				Jsonite.LOGGER.info("Set hardness");
			}
			if (jsonObject.has("resistance")) {
				settings.resistance(jsonObject.get("resistance").getAsFloat());
				Jsonite.LOGGER.info("Set resistance");
			}
			if (jsonObject.has("sounds")) {
				String sound = jsonObject.get("sounds").getAsString().toLowerCase();
				BlockSoundGroup soundGroup = switch (sound) {
					case "wood" -> BlockSoundGroup.WOOD;
					case "stone" -> BlockSoundGroup.STONE;
					case "metal" -> BlockSoundGroup.METAL;
					case "glass" -> BlockSoundGroup.GLASS;
					case "sand" -> BlockSoundGroup.SAND;
					case "gravel" -> BlockSoundGroup.GRAVEL;
					default -> BlockSoundGroup.STONE;
				};
				settings.sounds(soundGroup);
				Jsonite.LOGGER.info("Set sound group to {}", sound);
			}

			return settings;

		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to create Block.Settings from JSON: {}", jsonPath, e);
			return Settings.create().registryKey(key);
		}
	}

	private static void registerBlockFromJson(Path jsonPath, String modId) {
		Jsonite.LOGGER.info("Loading JSON file: {}", jsonPath);
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

			if (!jsonObject.has("name")) {
				Jsonite.LOGGER.error("Missing 'name' field in JSON file: {}", jsonPath);
				return;
			}

			String name = jsonObject.get("name").getAsString();
			Identifier id = Identifier.of(modId, name);

			// Create the registry key
			RegistryKey<Block> key = RegistryKey.of(
				Registries.BLOCK.getKey(),
				id
			);

			Settings settings = getBlockSettingsFromJson(jsonPath, key);
			Block block = new Block(settings);

			Block registeredBlock = registerBlock(name, block, modId, id);
			Jsonite.LOGGER.info("Successfully registered block: {} with registry name: {}", name, registeredBlock.getTranslationKey());

		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to register block from JSON: {}", jsonPath, e);
		}
	}

	public static void registerModBlocks(String packId) {
		Jsonite.LOGGER.info("Registering Mod Blocks for " + Jsonite.MOD_ID);

		Path resourcePacksPath = Paths.get("resourcepacks");

		try (Stream<Path> modFolders = Files.list(resourcePacksPath)) {
			modFolders.filter(Files::isDirectory).forEach(modFolder -> {
				String modId = modFolder.getFileName().toString();
				Path blocksFolder = modFolder.resolve("data").resolve(modId).resolve("blocks");

				if (!Files.exists(blocksFolder)) {
					Jsonite.LOGGER.warn("No blocks folder found for mod ID: {}", modId);
					return;
				}

				try (Stream<Path> paths = Files.walk(blocksFolder, 1)) {
					paths.filter(Files::isRegularFile)
						.filter(path -> path.toString().endsWith(".json"))
						.forEach(path -> registerBlockFromJson(path, modId));
				} catch (IOException e) {
					Jsonite.LOGGER.error("Failed to load blocks for mod ID: {}", modId, e);
				}
			});
		} catch (IOException e) {
			Jsonite.LOGGER.error("Failed to traverse resourcepacks folder", e);
		}
	}
	public static void clearRegisteredBlocks() {
		REGISTERED_BLOCKS.clear();
		Jsonite.LOGGER.info("Cleared all registered blocks");
	}
}
