package dev.hephaestus.glowcase.client.render.block.entity;

import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.widget.RecipeBackground;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import dev.hephaestus.glowcase.client.util.BlockEntityRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RecipeBlockEntityRenderer implements BlockEntityRenderer<RecipeBlockEntity> {
	private static BufferBuilderStorage SORRY = new BufferBuilderStorage(1);
	private static Identifier ITEM_TEXTURE = Glowcase.id("textures/item/recipe_block.png");
	private BlockEntityRendererFactory.Context context;

	public RecipeBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.context = context;
	}

	public void render(RecipeBlockEntity entity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Identifier rid = Identifier.tryParse(entity.recipe);
		EmiRecipe recipe;
		if ((rid == null || EmiApi.getRecipeManager().getRecipe(rid) == null) && entity.recipe.startsWith("xyzzy")) {
			String[] parts = entity.recipe.split(" ");
			List<EmiRecipe> recipes = EmiApi.getRecipeManager().getRecipes();
			if (parts.length == 2) {
				for (EmiRecipeCategory category : EmiApi.getRecipeManager().getCategories()) {
					if (category.getId().toString().equals(parts[1])) {
						recipes = EmiApi.getRecipeManager().getRecipes(category);
						break;
					}
				}
			}
			int c = (int) (entity.getPos().hashCode() ^ (System.currentTimeMillis() / 769));
			if (recipes.isEmpty()) {
				renderPlaceholder(entity, matrices, vertexConsumers);
				return;
			}
			recipe = recipes.get(new Random(c).nextInt(recipes.size()));
		} else {
			if (rid == null) {
				renderPlaceholder(entity, matrices, vertexConsumers);
				return;
			}
			recipe = EmiApi.getRecipeManager().getRecipe(rid);
		}
		if (recipe == null) {
			renderPlaceholder(entity, matrices, vertexConsumers);
			return;
		}
		matrices.push();
		matrices.translate(0.5D, 0.5D, 0.5D);

		float rotation = -(entity.getCachedState().get(Properties.ROTATION) * 360) / 16.0F;
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

		switch (entity.zOffset) {
			case FRONT -> matrices.translate(0D, 0D, -0.4D);
			case BACK -> matrices.translate(0D, 0D, 0.4D);
			case CENTER -> matrices.translate(0D, 0D, 0D);
		}

		int fullWidth = recipe.getDisplayWidth() + 8;
		int fullHeight = recipe.getDisplayHeight() + 8;
		Framebuffer fb = createFramebuffer(recipe);
		fb.beginRead();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, fb.getColorAttachment());
		RenderSystem.enableDepthTest();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder builder = tess.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		MatrixStack.Entry entry = matrices.peek();
		float xMin = -0.15f / 16 * fullWidth;
		float xMax =  0.15f / 16 * fullWidth;
		float yMin = -0.15f / 16 * fullHeight;
		float yMax =  0.15f / 16 * fullHeight;
		builder.vertex(entry, xMin, yMax, 0).color(255, 255, 255, 255).texture(1, 1);
		builder.vertex(entry, xMax, yMax, 0).color(255, 255, 255, 255).texture(0, 1);
		builder.vertex(entry, xMax, yMin, 0).color(255, 255, 255, 255).texture(0, 0);
		builder.vertex(entry, xMin, yMin, 0).color(255, 255, 255, 255).texture(1, 0);
		BufferRenderer.drawWithGlobalProgram(builder.end());
		fb.delete();
		MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

		matrices.pop();
	}

	private void renderPlaceholder(RecipeBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
		if (BlockEntityRenderUtil.shouldRenderPlaceholder(entity.getPos())) {
			Camera camera = context.getRenderDispatcher().camera;
			BlockEntityRenderUtil.renderPlaceholder(entity, ITEM_TEXTURE, 1F, matrices, vertexConsumers, camera);
		}
	}

	private static Framebuffer createFramebuffer(EmiRecipe recipe) {
		MinecraftClient client = MinecraftClient.getInstance();
		int width = recipe.getDisplayWidth() + 8;
		int height = recipe.getDisplayHeight() + 8;
		int scale = 4;
		Framebuffer framebuffer = new SimpleFramebuffer(width * scale, height * scale, true, MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.setClearColor(0f, 0f, 0f, 0f);
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.beginWrite(true);

		Matrix4fStack view = RenderSystem.getModelViewStack();
		view.pushMatrix();
		view.identity();
		view.translate(-1.0f, 1.0f, 0.0f);
		view.scale(2f / width, -2f / height, -1f / 1000f);
		view.translate(0.0f, 0.0f, 10.0f);
		RenderSystem.applyModelViewMatrix();

		Matrix4f backupProj = RenderSystem.getProjectionMatrix();
		RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorter.BY_Z);
		GlowcaseWidgetHolder holder = new GlowcaseWidgetHolder(recipe.getDisplayWidth(), recipe.getDisplayHeight());
		holder.widgets.add(new RecipeBackground(-4, -4, recipe.getDisplayWidth() + 8, recipe.getDisplayHeight() + 8));
		recipe.addWidgets(holder);
		// getEffectVertexConsumers doesn't cause random rendering issues like getEntityVertexConsumers
		DrawContext context = new DrawContext(client, SORRY.getEntityVertexConsumers());
		context.getMatrices().translate(4, 4, 0);
		for (Widget widget : holder.widgets) {
			widget.render(context, -9999, -9999, 0);
		}
		// Magic incantation/desperate prayer
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		DiffuseLighting.enableForLevel();

		RenderSystem.setProjectionMatrix(backupProj, VertexSorter.BY_DISTANCE);
		view.popMatrix();
		RenderSystem.applyModelViewMatrix();
		SORRY.getEntityVertexConsumers().draw();

		framebuffer.endWrite();
		client.getFramebuffer().beginWrite(true);
		return framebuffer;
	}

	private static class GlowcaseWidgetHolder implements WidgetHolder {
		private final int width, height;
		public final List<Widget> widgets = Lists.newArrayList();

		public GlowcaseWidgetHolder(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public <T extends Widget> T add(T widget) {
			widgets.add(widget);
			return widget;
		}
	}
}
