package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemProviderBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class ItemProviderBlockEditScreen extends GlowcaseScreen {

	private final ItemProviderBlockEntity providerBlock;
	private ButtonWidget givesItemButton;
	private ButtonWidget rotationTypeButton;
	private ButtonWidget showNameButton;
	private ButtonWidget offsetButton;

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

			this.givesItemButton = ButtonWidget.builder(Text.stringifiedTranslatable("gui.glowcase.gives_item", this.providerBlock.givesItem), (action) -> {
				this.providerBlock.cycleGiveType();
				this.givesItemButton.setMessage(Text.stringifiedTranslatable("gui.glowcase.gives_item", this.providerBlock.givesItem));
				editItemDisplayBlock(true);
			}).dimensions(centerW - 75, centerH - 40 - individualPadding, 150, 20).build();

			this.addDrawableChild(this.givesItemButton);
		}
	}

	private void editItemDisplayBlock(boolean updatePitchAndYaw) {
		if (updatePitchAndYaw && MinecraftClient.getInstance().getCameraEntity() != null) {
			Vec2f pitchAndYaw = ItemProviderBlockEntity.getPitchAndYaw(MinecraftClient.getInstance().getCameraEntity(), providerBlock.getPos(), 0);
			providerBlock.pitch = pitchAndYaw.x;
			providerBlock.yaw = pitchAndYaw.y;
		}
		C2SEditItemProviderBlock.of(providerBlock).send();
	}
}
