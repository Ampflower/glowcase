package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NoteEditScreen extends GlowcaseScreen {
	private static final Identifier TEXTURE = Glowcase.id("textures/gui/note.png");

	public NoteEditScreen(ItemStack stack) {
		// TODO: Extract component here
	}

	@Override
	protected void init() {
		super.init();
		if (this.client == null) return;

	}
}
