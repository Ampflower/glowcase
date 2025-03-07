package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.AbstractDisplayBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemDisplayBlock;

public class ItemDisplayEditScreen extends AbstractDisplayBlockEditScreen {
	public ItemDisplayEditScreen(AbstractDisplayBlockEntity displayBlock) {
		super(displayBlock);
	}

	@Override
	protected void editDisplayBlock() {
		super.editDisplayBlock();
		C2SEditItemDisplayBlock.of(displayBlock).send();
	}
}
