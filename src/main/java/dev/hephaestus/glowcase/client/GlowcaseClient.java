package dev.hephaestus.glowcase.client;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.render.block.entity.*;
import dev.hephaestus.glowcase.client.render.item.ItemHandRenderer;
import dev.hephaestus.glowcase.client.render.item.NoteItemHandRenderer;
import dev.hephaestus.glowcase.client.render.item.TabletItemHandRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class GlowcaseClient implements ClientModInitializer {
	public static final ScreenImageCache screenImageCache = new ScreenImageCache();

	@Override
	public void onInitializeClient() {
		Glowcase.proxy = new GlowcaseClientProxy();

		BlockEntityRendererFactories.register(Glowcase.TEXT_BLOCK_ENTITY.get(), TextBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.HYPERLINK_BLOCK_ENTITY.get(), HyperlinkBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.ITEM_DISPLAY_BLOCK_ENTITY.get(), ItemDisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.POPUP_BLOCK_ENTITY.get(), PopupBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.SCREEN_BLOCK_ENTITY.get(), ScreenBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.SPRITE_BLOCK_ENTITY.get(), SpriteBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.OUTLINE_BLOCK_ENTITY.get(), OutlineBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.PARTICLE_DISPLAY_BLOCK_ENTITY.get(), ParticleDisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.SOUND_BLOCK_ENTITY.get(), SoundPlayerBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(Glowcase.ITEM_ACCEPTOR_BLOCK_ENTITY.get(), ItemAcceptorBlockEntityRenderer::new);

		ItemHandRenderer.register(Glowcase.TABLET_ITEM.get().asItem(), new TabletItemHandRenderer());
		ItemHandRenderer.register(Glowcase.NOTE_ITEM.get().asItem(), new NoteItemHandRenderer());

		WorldRenderEvents.AFTER_TRANSLUCENT.register(BakedBlockEntityRenderer.Manager::render);
		InvalidateRenderStateCallback.EVENT.register(BakedBlockEntityRenderer.Manager::reset);
	}
}
