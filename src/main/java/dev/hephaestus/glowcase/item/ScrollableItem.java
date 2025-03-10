package dev.hephaestus.glowcase.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ScrollableItem {
	void scroll(ItemStack caseStack, PlayerEntity player, int amount);
}
