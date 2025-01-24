package io.github.beez131github.jsonite.block;

import io.github.beez131github.jsonite.Jsonite;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class ModBlocks {



	private static Block registerBlockWithoutBlockItem(String name, Block block) {
		return Registry.register(Registries.BLOCK, Identifier.of(Jsonite.MOD_ID, name), block);
	}

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, Identifier.of(Jsonite.MOD_ID, name), block);
	}

	private static void registerBlockItem(String name, Block block) {
		Registry.register(Registries.ITEM, Identifier.of(Jsonite.MOD_ID, name),
			new BlockItem(block, new Item.Settings()));
	}

	public static void registerModBlocks() {
		Jsonite.LOGGER.info("Registering Mod Blocks for " + Jsonite.MOD_ID);
	}
}
