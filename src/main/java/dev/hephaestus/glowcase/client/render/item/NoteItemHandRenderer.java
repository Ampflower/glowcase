package dev.hephaestus.glowcase.client.render.item;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class NoteItemHandRenderer extends ItemHandRenderer {
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {

	}

	@Override
	public boolean visible(ItemStack stack) {
		return false;
	}
}
