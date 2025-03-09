package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemProviderBlock;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ItemProviderBlockEditScreen extends GlowcaseScreen {

	private final ItemProviderBlockEntity providerBlock;
	private ButtonWidget givesItemButton;
	public ItemProviderBlockEditScreen(ItemProviderBlockEntity providerBlock) {
		this.providerBlock = providerBlock;
	}

	@Override
	public void init() {
		super.init();

		if (this.client != null) {
			int padding = width / 100;
			int individualPadding = padding / 2;
			int centerW = width / 2;
			int centerH = height / 2;

			this.givesItemButton = ButtonWidget.builder(Text.stringifiedTranslatable("gui.glowcase.gives_item", this.providerBlock.getGivesItem()), (action) -> {
				this.providerBlock.cycleGiveType();
				this.givesItemButton.setMessage(Text.stringifiedTranslatable("gui.glowcase.gives_item", this.providerBlock.getGivesItem()));
				editItemDisplayBlock();
			}).dimensions(centerW - 75, centerH - 40 - individualPadding, 150, 20).build();

			this.addDrawableChild(this.givesItemButton);
		}
	}

	private void editItemDisplayBlock() {
		C2SEditItemProviderBlock.of(providerBlock).send();
	}
}
