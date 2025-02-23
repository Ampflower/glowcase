package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.ItemDisplayBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemDisplayBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class ItemDisplayBlockEditScreen extends GlowcaseScreen {
	private final ItemDisplayBlockEntity displayBlock;

//	private ButtonWidget givesItemButton;
	private ButtonWidget rotationTypeButton;
	private ButtonWidget showNameButton;
	private ButtonWidget offsetButton;
	private ButtonWidget decreaseSize;
	private ButtonWidget increaseSize;

	private ButtonWidget decreaseXOffset;
	private ButtonWidget increaseXOffset;
	private ButtonWidget decreaseYOffset;
	private ButtonWidget increaseYOffset;
	private ButtonWidget decreaseZOffset;
	private ButtonWidget increaseZOffset;

	public ItemDisplayBlockEditScreen(ItemDisplayBlockEntity displayBlock) {
		this.displayBlock = displayBlock;
	}

	@Override
	public void init() {
		super.init();

		if (this.client != null) {
			int padding = width / 100;
			int individualPadding = padding / 2;
			int centerW = width / 2;
			int centerH = height / 2;

			this.decreaseSize = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.scale -= (float) Math.max(0, 0.125);
				this.displayBlock.scale = MathHelper.clamp(this.displayBlock.scale, -10F,10F);
				editItemDisplayBlock(true);
			}).dimensions(90, 0, 20, 20).build();

			this.increaseSize = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.scale += 0.125F;
				this.displayBlock.scale = MathHelper.clamp(this.displayBlock.scale, -10F,10F);
				editItemDisplayBlock(true);
			}).dimensions(110, 0, 20, 20).build();

			this.decreaseXOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.xOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.xOffset = MathHelper.clamp(this.displayBlock.xOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(90, 30, 20, 20).build();

			this.increaseXOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.xOffset += 0.125F;
				this.displayBlock.xOffset = MathHelper.clamp(this.displayBlock.xOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(110, 30, 20, 20).build();

			this.decreaseYOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.yOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.yOffset = MathHelper.clamp(this.displayBlock.yOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(90, 60, 20, 20).build();

			this.increaseYOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.yOffset += 0.125F;
				this.displayBlock.yOffset = MathHelper.clamp(this.displayBlock.yOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(110, 60, 20, 20).build();

			this.decreaseZOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.zOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.zOffset = MathHelper.clamp(this.displayBlock.zOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(90, 90, 20, 20).build();

			this.increaseZOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.zOffset += 0.125F;
				this.displayBlock.zOffset = MathHelper.clamp(this.displayBlock.zOffset, -5F,5F);
				editItemDisplayBlock(true);
			}).dimensions(110, 90, 20, 20).build();

			this.rotationTypeButton = ButtonWidget.builder(Text.stringifiedTranslatable("gui.glowcase.rotation_type", this.displayBlock.rotationType), (action) -> {
				this.displayBlock.cycleRotationType(this.client.player);
				this.rotationTypeButton.setMessage(Text.stringifiedTranslatable("gui.glowcase.rotation_type", this.displayBlock.rotationType));
				editItemDisplayBlock(true);
			}).dimensions(centerW - 75, centerH - 20, 150, 20).build();

			this.showNameButton = ButtonWidget.builder(Text.translatable("gui.glowcase.show_name", this.displayBlock.showName), (action) -> {
				this.displayBlock.showName = !this.displayBlock.showName;
				this.showNameButton.setMessage(Text.translatable("gui.glowcase.show_name", this.displayBlock.showName));
				editItemDisplayBlock(false);
			}).dimensions(centerW - 75, centerH + individualPadding, 150, 20).build();

			this.offsetButton = ButtonWidget.builder(Text.stringifiedTranslatable("gui.glowcase.offset_value", this.displayBlock.offset), (action) -> {
				this.displayBlock.cycleOffset();
				this.offsetButton.setMessage(Text.stringifiedTranslatable("gui.glowcase.offset_value", this.displayBlock.offset));
				editItemDisplayBlock(true);
			}).dimensions(centerW - 75, centerH + 20 + padding, 150, 20).build();

			this.addDrawableChild(this.decreaseSize);
			this.addDrawableChild(this.increaseSize);
			this.addDrawableChild(this.decreaseXOffset);
			this.addDrawableChild(this.increaseXOffset);
			this.addDrawableChild(this.decreaseYOffset);
			this.addDrawableChild(this.increaseYOffset);
			this.addDrawableChild(this.decreaseZOffset);
			this.addDrawableChild(this.increaseZOffset);
			this.addDrawableChild(this.rotationTypeButton);
			this.addDrawableChild(this.showNameButton);
			this.addDrawableChild(this.offsetButton);
		}
	}
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if(this.client != null) {
			super.render(context, mouseX, mouseY, delta);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.scale_value", this.displayBlock.scale), 7, 7, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase_x_offset_value", this.displayBlock.xOffset), 7, 37, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase_y_offset_value", this.displayBlock.yOffset), 7, 67, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase_z_offset_value", this.displayBlock.zOffset), 7, 97, 0xFFFFFFFF);
		}
	}


	private void editItemDisplayBlock(boolean updatePitchAndYaw) {
		if (updatePitchAndYaw && MinecraftClient.getInstance().getCameraEntity() != null) {
			Vec2f pitchAndYaw = ItemDisplayBlockEntity.getPitchAndYaw(MinecraftClient.getInstance().getCameraEntity(), displayBlock.getPos(), 0);
			displayBlock.pitch = pitchAndYaw.x;
			displayBlock.yaw = pitchAndYaw.y;
		}
		C2SEditItemDisplayBlock.of(displayBlock).send();
	}
}
