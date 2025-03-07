package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.AbstractDisplayBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditEntityDisplayBlock;

public class EntityDisplayEditScreen extends AbstractDisplayBlockEditScreen {
	public EntityDisplayEditScreen(AbstractDisplayBlockEntity displayBlock) {
		super(displayBlock);
	}

	@Override
	protected void editDisplayBlock() {
		super.editDisplayBlock();
		C2SEditEntityDisplayBlock.of(displayBlock).send();
	}
}
