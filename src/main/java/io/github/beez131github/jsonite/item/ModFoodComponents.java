package io.github.beez131github.jsonite.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.beez131github.jsonite.Jsonite;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModFoodComponents {
	private static final Map<String, FoodComponent> FOOD_COMPONENTS = new HashMap<>();
	private static final Map<String, ConsumableComponent> CONSUMABLE_COMPONENTS = new HashMap<>();

	public static FoodComponent getFoodComponent(String name) {
		return FOOD_COMPONENTS.get(name);
	}

	public static ConsumableComponent getConsumableComponent(String name) {
		return CONSUMABLE_COMPONENTS.get(name);
	}

	private static void loadFoodComponents(Path jsonPath, String modId) {
		try {
			String jsonContent = Files.readString(jsonPath);
			JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

			String name = jsonObject.get("name").getAsString();
			JsonObject foodData = jsonObject.getAsJsonObject("food");

			FoodComponent.Builder foodBuilder = new FoodComponent.Builder();
			foodBuilder.nutrition(foodData.get("hunger").getAsInt())
				.saturationModifier(foodData.get("saturation").getAsFloat());

			FoodComponent foodComponent = foodBuilder.build();
			FOOD_COMPONENTS.put(name, foodComponent);

			// Register the food item
			Identifier id = Identifier.of(modId, name);
			RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
			Item.Settings settings = new Item.Settings().registryKey(key).food(foodComponent);
			Registry.register(Registries.ITEM, id, new Item(settings));

			Jsonite.LOGGER.info("Loaded and registered food item: {}", name);

		} catch (Exception e) {
			Jsonite.LOGGER.error("Failed to load food component from JSON: {}", jsonPath, e);
		}
	}

	public static void registerModFoods(String packId) {
		Jsonite.LOGGER.info("Registering Mod Foods for " + Jsonite.MOD_ID);

		Path resourcePacksPath = Paths.get("resourcepacks");

		try (Stream<Path> modFolders = Files.list(resourcePacksPath)) {
			modFolders.filter(Files::isDirectory).forEach(modFolder -> {
				String modId = modFolder.getFileName().toString();

				Path foodsFolder = modFolder.resolve("data").resolve(modId).resolve("foods");

				if (!Files.exists(foodsFolder)) {
					Jsonite.LOGGER.warn("No foods folder found for mod ID: {}", modId);
					return;
				}

				try (Stream<Path> paths = Files.walk(foodsFolder, 1)) {
					paths.filter(Files::isRegularFile)
						.filter(path -> path.toString().endsWith(".json"))
						.forEach(path -> loadFoodComponents(path, modId));
				} catch (IOException e) {
					Jsonite.LOGGER.error("Failed to load foods for mod ID: {}", modId, e);
				}
			});
		} catch (IOException e) {
			Jsonite.LOGGER.error("Failed to traverse resourcepacks folder", e);
		}
	}
	public static void clearRegisteredFoods() {
		FOOD_COMPONENTS.clear();
		CONSUMABLE_COMPONENTS.clear();
		Jsonite.LOGGER.info("Cleared all registered food items");
	}

}
