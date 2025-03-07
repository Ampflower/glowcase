package dev.hephaestus.glowcase.client.render.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ItemDisplayBlockEntity;
import dev.hephaestus.glowcase.client.util.BlockEntityRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public record ItemDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<ItemDisplayBlockEntity> {
	public static Identifier ITEM_TEXTURE = Glowcase.id("textures/item/item_display_block.png");

	@Override
	public void render(ItemDisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Entity camera = MinecraftClient.getInstance().getCameraEntity();

		if (camera == null) return;

		matrices.push();
		matrices.translate(0.5D, 0D, 0.5D);

		float pitch = entity.getPitch();
		float yaw = entity.getYaw();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
		matrices.translate(entity.getOffset().x(), entity.getOffset().y(), entity.getOffset().z());
		matrices.translate(0, 0.5, 0);
		matrices.scale(entity.getScale().x(), entity.getScale().y(), entity.getScale().z());
		matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch));
		context.getItemRenderer().renderItem(entity.getStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

		matrices.pop();

		if (!entity.matchesStack(ItemStack.EMPTY) || BlockEntityRenderUtil.shouldRenderPlaceholder(entity.getPos())) BlockEntityRenderUtil.renderPlaceholder(entity, ITEM_TEXTURE, 1.0F, RotationAxis.POSITIVE_Y.rotationDegrees(180), matrices, vertexConsumers, context.getRenderDispatcher().camera, 0f);
	}
}
