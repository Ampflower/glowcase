package dev.hephaestus.glowcase.client.render.item;

import com.google.common.collect.Maps;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Called when the item is being held in hand.
 */
public abstract class ItemHandRenderer {
	/**
	 * Called on each frame when the player is holding the given stack.
	 */
	public abstract void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack);

	/**
	 * Whenever the item should be rendered or not.
	 * Returning false falls back to the default rendering method of the item.
	 */
	public boolean visible(ItemStack stack) {
		return true;
	}

	// End of class

	private static final Map<Item, ItemHandRenderer> RENDERER = Maps.newHashMap();

	public static void register(Item item, ItemHandRenderer factory) {
		RENDERER.put(item, factory);
	}

	/**
	 * Returns the item hand renderer of the given stack or null,
	 * if not found or if it should be skipped.
	 */
	public static @Nullable ItemHandRenderer getRenderer(ItemStack stack) {
		for (Item item : RENDERER.keySet())
			if (stack.isOf(item))
				return RENDERER.get(item);

		return null;
	}
}
