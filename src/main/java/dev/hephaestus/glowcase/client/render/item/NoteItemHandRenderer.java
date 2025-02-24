package dev.hephaestus.glowcase.client.render.item;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NoteItemHandRenderer extends ItemHandRenderer {
	private static final Identifier NOTE_TEXTURE = Glowcase.id("textures/gui/note.png");

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {

	}

	@Override
	public boolean visible(ItemStack stack) {
		return (stack.contains(Glowcase.NOTE_COMPONENT.get()));
	}
}
