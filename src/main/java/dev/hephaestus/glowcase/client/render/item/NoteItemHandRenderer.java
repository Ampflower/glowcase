package dev.hephaestus.glowcase.client.render.item;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.gui.screen.ingame.NoteEditScreen;
import dev.hephaestus.glowcase.client.util.NoteTextColorResource;
import dev.hephaestus.glowcase.item.component.NoteComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.List;

public class NoteItemHandRenderer extends ItemHandRenderer {
	private static final Identifier NOTE_TEXTURE = Glowcase.id("textures/gui/note.png");

	private static final int BG_SIZE = 256;

	private static final int BG_WIDTH = 244 ;
	private static final int BG_HEIGHT = 117;

	private static final int TXT_X_PADDING = 15 * 2;

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
		matrices.scale(0.38F, 0.38F, 0.38F);
		matrices.translate(-0.5F, -0.5F, 0.0F);
		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

		// Render background

		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(NOTE_TEXTURE));
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();

		{
			float max_x = 1.0F / BG_SIZE * BG_WIDTH;
			float max_y = 1.0F / BG_SIZE * BG_HEIGHT;

			float scaler = 20F;
			float begin = -7F - scaler;
			float end = 135F + scaler;

			float x_off = 10F;

			float height = (Math.abs(begin) + end) / BG_SIZE * BG_HEIGHT;
			float y_off = -20F;
			float y1 = begin + ((Math.abs(begin) + end) - height) + y_off;
			end += y_off;

			vertexConsumer.vertex(matrix4f, x_off + begin, end, 0.0F).color(Colors.WHITE).texture(0.0F, max_y).light(light);
			vertexConsumer.vertex(matrix4f, x_off + end, end, 0.0F).color(Colors.WHITE).texture(max_x, max_y).light(light);
			vertexConsumer.vertex(matrix4f, x_off + end, y1, 0.0F).color(Colors.WHITE).texture(max_x, 0.0F).light(light);
			vertexConsumer.vertex(matrix4f, x_off + begin, y1, 0.0F).color(Colors.WHITE).texture(0.0F, 0.0F).light(light);
		}

		// Render Text

		NoteComponent noteComponent = stack.get(Glowcase.NOTE_COMPONENT.get());
		if (noteComponent == null) {
			matrices.pop();
			return;
		}

		float off_x = -7F;
		float off_y = 63F;

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		matrices.translate(off_x, off_y, -.01f);
		matrices.scale(0.67f, 0.67f, 1f);

		float width = BG_WIDTH - TXT_X_PADDING;

		List<Text> lines = noteComponent.lines();
		for (int i=0; i<lines.size(); i++) {
			StringVisitable text = lines.get(i);
			if (!NoteEditScreen.isInBounds(textRenderer, text))
				text = NoteEditScreen.ensureBounds(textRenderer, text);

			float x = switch (noteComponent.alignment()) {
				case LEFT -> 0;
				case CENTER -> width/2f - textRenderer.getWidth(text)/2f - 1; // We don't ask why the -1 is there
				case RIGHT ->  BG_WIDTH - TXT_X_PADDING - textRenderer.getWidth(text);
			};

			textRenderer.draw(Language.getInstance().reorder(text), x, textRenderer.fontHeight * i, NoteTextColorResource.TXT_COLOR, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
		}

		matrices.pop();
	}

	@Override
	public boolean visible(ItemStack stack) {
		return (stack.contains(Glowcase.NOTE_COMPONENT.get()));
	}
}
