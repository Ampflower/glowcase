package dev.hephaestus.glowcase.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface InfiniteInventory extends Inventory {
	ItemStack getStack();

	default boolean hasItem() {
		return getStack() != null && !getStack().isEmpty();
	}

	@Override
	default int size() {
		return 1;
	}

	@Override
	default boolean isEmpty() {
		return getStack().isEmpty();
	}

	@Override
	default ItemStack getStack(int slot) {
		return getStack().copyWithCount(1);
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		return getStack().copyWithCount(1);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return getStack().copyWithCount(1);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
	}

	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	default void clear() {
	}
}
