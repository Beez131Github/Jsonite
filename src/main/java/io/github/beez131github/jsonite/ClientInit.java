package io.github.beez131github.jsonite;

import io.github.beez131github.jsonite.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class ClientInit implements ClientModInitializer {
	private static final DisconnectionDetails SERVER_MISSING_MOD = new DisconnectionDetails(Text.translatable(
		"multiplayer.jsonite.disconnect.server_missing_mod"
	));

	private static boolean validServer;

	@Override
	public void onInitializeClient() {
		ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> validServer = false);

		ClientLoginNetworking.registerGlobalReceiver(
			Jsonite.VERIFICATION_CHANNEL,
			(client, handler, buf, listenerAdder) -> {
				validServer = true;
				return CompletableFuture.completedFuture(PacketByteBufs.empty());
			}
		);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (!validServer) {
				handler.onDisconnected(SERVER_MISSING_MOD);
			}
		});

		System.out.println("Growable golden carrots initialized!");
	}
}
