package dev.hephaestus.glowcase.client;

import java.util.List;

import com.google.common.collect.Lists;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.render.block.entity.BakedBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.HyperlinkBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.ItemAcceptorBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.ItemDisplayBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.OutlineBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.ParticleDisplayBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.PopupBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.RecipeBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.SoundPlayerBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.SpriteBlockEntityRenderer;
import dev.hephaestus.glowcase.client.render.block.entity.TextBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.mixin.object.builder.client.ModelPredicateProviderRegistryAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class GlowcaseClient implements ClientModInitializer {
	public static final Boolean EMI_LOADED = FabricLoader.getInstance().isModLoaded("emi");

	@Override
	public void onInitializeClient() {
		Glowcase.proxy = new GlowcaseClientProxy();

		BlockEntityRendererFactories.register(Glowcase.TEXT_BLOCK_ENTITY.get(), TextBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.HYPERLINK_BLOCK_ENTITY.get(), HyperlinkBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.ITEM_DISPLAY_BLOCK_ENTITY.get(), ItemDisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.POPUP_BLOCK_ENTITY.get(), PopupBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.SPRITE_BLOCK_ENTITY.get(), SpriteBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.RECIPE_BLOCK_ENTITY.get(), RecipeBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.OUTLINE_BLOCK_ENTITY.get(), OutlineBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.PARTICLE_DISPLAY_BLOCK_ENTITY.get(), ParticleDisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.SOUND_BLOCK_ENTITY.get(), SoundPlayerBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.ITEM_ACCEPTOR_BLOCK_ENTITY.get(), ItemAcceptorBlockEntityRenderer::new);

		WorldRenderEvents.AFTER_TRANSLUCENT.register(BakedBlockEntityRenderer.Manager::render);
		InvalidateRenderStateCallback.EVENT.register(BakedBlockEntityRenderer.Manager::reset);

		ModelPredicateProviderRegistryAccessor.callRegister(Identifier.of("glowcase:awakened"), (stack, world, entity, seed) -> {
			if (!EMI_LOADED) {
				return 0;
			}
			List<ItemStack> testStacks = Lists.newArrayList();
			if (entity != null) {
				testStacks.add(entity.getMainHandStack());
				testStacks.add(entity.getOffHandStack());
			}
			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity player = client.player;
			if (player != null) {
				ScreenHandler handler = player.currentScreenHandler;
				if (handler != null) {
					testStacks.add(handler.getCursorStack());
				}
			}
			for (ItemStack s : testStacks) {
				if (s == stack) {
					return 1;
				}
			}
			return 0;
		});
	}
}
