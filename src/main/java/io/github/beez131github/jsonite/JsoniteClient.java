package io.github.beez131github.jsonite;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class JsoniteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Block goldenCarrotCrop = Registries.BLOCK.get(Identifier.of("grow_gc", "golden_carrot_crop"));
		BlockRenderLayerMap.INSTANCE.putBlock(goldenCarrotCrop, RenderLayer.getCutout());
	}
}
