package io.github.beez131github.jsonite.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import io.github.beez131github.jsonite.block.ModBlocks;
import net.minecraft.data.client.*;
import net.minecraft.data.client.model.BlockStateModelGenerator;

public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {


	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {

	}
}
