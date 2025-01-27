package io.github.beez131github.jsonite;

import io.github.beez131github.jsonite.block.ModBlocks;
import io.github.beez131github.jsonite.item.ModFoodComponents;
import io.github.beez131github.jsonite.item.ModItems;
import io.github.beez131github.jsonite.item.ModWeapons;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener.Synchronizer;

public class ResourceHandler implements ResourceReloadListener {
    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(() -> {
            if (manager instanceof ServerResourcePackManager packManager) {
                // Clear all existing registrations
                ModItems.clearRegisteredItems();
                ModBlocks.clearRegisteredBlocks();
                ModWeapons.clearRegisteredWeapons();
                ModFoodComponents.clearRegisteredFoods();

                // Get currently enabled packs
                Collection<ResourcePack> enabledPacks = packManager.getEnabledProfiles();

                // Register content from each enabled pack
                for (ResourcePack pack : enabledPacks) {
                    String packId = pack.getName();
                    Jsonite.LOGGER.info("Loading content from pack: {}", packId);

                    ModItems.registerModItems(packId);
                    ModBlocks.registerModBlocks(packId);
                    ModWeapons.registerModWeapons(packId);
                    ModFoodComponents.registerModFoods(packId);
                }
            }
        }, prepareExecutor);
    }
}
