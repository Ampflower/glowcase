package dev.hephaestus.glowcase.client.render.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.ItemProviderBlock;
import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import dev.hephaestus.glowcase.client.util.BlockEntityRenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;

public record ItemProviderBlockEntityRenderer(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<ItemProviderBlockEntity> {

	public static Identifier ITEM_TEXTURE = Glowcase.id("textures/item/item_provider_block.png");
	@Override
	public void render(ItemProviderBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Entity camera = MinecraftClient.getInstance().getCameraEntity();
		BlockState blockState = entity.getWorld().getBlockState(entity.getPos());

		if (camera == null) return;

		matrices.push();
		matrices.translate(0.5D, 0D, 0.5D);

		float yaw = 0F;
		float pitch = 0F;

		boolean isBack = false;
		boolean isBillboard = false;

        switch (blockState.get(ItemProviderBlock.FACING)) {
			case DOWN, UP -> {
				if (entity.getStack().getItem() instanceof BlockItem) {
					Vec2f pitchAndYaw = ItemProviderBlockEntity.getPitchAndYaw(camera, entity.getPos(), tickDelta);
					pitch = pitchAndYaw.x;
					yaw = pitchAndYaw.y;
					matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
				} else {
					pitch = (float) Math.toRadians(camera.getPitch());
					yaw = (float) Math.toRadians(-camera.getYaw());
					matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
					isBillboard = true;
				}
			}
	        default -> {
		        var rotation = -(entity.getCachedState().get(Properties.FACING).asRotation() * 2 * Math.PI) / 16.0F;
		        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) rotation));
		        matrices.translate(0D, Math.sin(pitch) * -0.4, 0.4D);
		        isBack = true;
	        }
		}

		ItemStack stack = entity.getStack();
		Text name;
		if (stack.getItem() instanceof SpawnEggItem) {
			matrices.push();
			name = Text.empty();
			matrices.pop();
			matrices.translate(0, 0.125F, 0);
			matrices.scale(0.5F, 0.5F, 0.5F);
		} else {
			name = stack.isEmpty() ? Text.translatable("gui.glowcase.none") : (Text.literal("")).append(stack.getName()).formatted(stack.getRarity().getFormatting());
			matrices.translate(0, 0.5, 0);
			matrices.scale(0.5F, 0.5F, 0.5F);
			matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch));
			context.getItemRenderer().renderItem(entity.getStack(), ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
		}

		HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
		if (hitResult instanceof BlockHitResult && ((BlockHitResult) hitResult).getBlockPos().equals(entity.getPos())) {
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
			matrices.translate(0, 0, -0.4);

			float scale = 0.025F;
			matrices.scale(scale, scale, scale);

			int color = name.getStyle().getColor() == null ? 0xFFFFFF : name.getStyle().getColor().getRgb();
			matrices.translate(-context.getTextRenderer().getWidth(name) / 2F, -4, 0);
			context.getTextRenderer().draw(name, 0, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}

		matrices.pop();

		if (!entity.hasItem() || BlockEntityRenderUtil.shouldRenderPlaceholder(entity.getPos())) {
			if(isBack) {
				BlockEntityRenderUtil.renderPlaceholderAtBack(entity, ITEM_TEXTURE, 1.0F, RotationAxis.POSITIVE_Y.rotationDegrees(180), matrices, vertexConsumers, context.getRenderDispatcher().camera);
			}
			else if(isBillboard) {
				BlockEntityRenderUtil.renderPlaceholderAsBillboard(entity, ITEM_TEXTURE, 1.0F, RotationAxis.POSITIVE_Y.rotationDegrees(180), matrices, vertexConsumers, context.getRenderDispatcher().camera);
			}
			else {
				BlockEntityRenderUtil.renderPlaceholder(entity, ITEM_TEXTURE, 1.0F, matrices, vertexConsumers, context.getRenderDispatcher().camera);
			}

		}

	}
}
