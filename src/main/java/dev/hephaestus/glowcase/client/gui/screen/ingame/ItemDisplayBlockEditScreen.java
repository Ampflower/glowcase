package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.ItemDisplayBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemDisplayBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ItemDisplayBlockEditScreen extends GlowcaseScreen {
	private final ItemDisplayBlockEntity displayBlock;
	private ButtonWidget decreaseSize;
	private ButtonWidget increaseSize;

	private ButtonWidget decreaseXOffset;
	private ButtonWidget increaseXOffset;
	private ButtonWidget decreaseYOffset;
	private ButtonWidget increaseYOffset;
	private ButtonWidget decreaseZOffset;
	private ButtonWidget increaseZOffset;
	private ButtonWidget decreasePitch;
	private ButtonWidget increasePitch;
	private ButtonWidget decreaseYaw;
	private ButtonWidget increaseYaw;
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
				editItemDisplayBlock();
			}).dimensions(90, 0, 20, 20).build();

			this.increaseSize = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.scale += 0.125F;
				this.displayBlock.scale = MathHelper.clamp(this.displayBlock.scale, -10F,10F);
				editItemDisplayBlock();
			}).dimensions(110, 0, 20, 20).build();

			this.decreaseXOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.xOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.xOffset = MathHelper.clamp(this.displayBlock.xOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(90, 30, 20, 20).build();

			this.increaseXOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.xOffset += 0.125F;
				this.displayBlock.xOffset = MathHelper.clamp(this.displayBlock.xOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(110, 30, 20, 20).build();

			this.decreaseYOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.yOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.yOffset = MathHelper.clamp(this.displayBlock.yOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(90, 60, 20, 20).build();

			this.increaseYOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.yOffset += 0.125F;
				this.displayBlock.yOffset = MathHelper.clamp(this.displayBlock.yOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(110, 60, 20, 20).build();

			this.decreaseZOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.zOffset -= (float) Math.max(0, 0.125);
				this.displayBlock.zOffset = MathHelper.clamp(this.displayBlock.zOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(90, 90, 20, 20).build();

			this.increaseZOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.zOffset += 0.125F;
				this.displayBlock.zOffset = MathHelper.clamp(this.displayBlock.zOffset, -5F,5F);
				editItemDisplayBlock();
			}).dimensions(110, 90, 20, 20).build();

			this.decreasePitch = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.pitch -= (float) Math.max(0, 0.0174533F);
				this.displayBlock.pitch = MathHelper.clamp(this.displayBlock.pitch, -6.28319F,6.28319F);
				editItemDisplayBlock();
			}).dimensions(90, 120, 20, 20).build();

			this.increasePitch = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.pitch += 0.0174533F;
				this.displayBlock.pitch = MathHelper.clamp(this.displayBlock.pitch, -6.28319F,6.28319F);
				editItemDisplayBlock();
			}).dimensions(110, 120, 20, 20).build();

			this.decreaseYaw = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.yaw -= (float) Math.max(0, 0.0174533);
				this.displayBlock.yaw = MathHelper.clamp(this.displayBlock.yaw, -6.28319F,6.28319F);
				editItemDisplayBlock();
			}).dimensions(90, 150, 20, 20).build();

			this.increaseYaw = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.yaw += 0.0174533F;
				this.displayBlock.yaw = MathHelper.clamp(this.displayBlock.yaw, -6.28319F,6.28319F);
				editItemDisplayBlock();
			}).dimensions(110, 150, 20, 20).build();

			this.addDrawableChild(this.decreaseSize);
			this.addDrawableChild(this.increaseSize);
			this.addDrawableChild(this.decreaseXOffset);
			this.addDrawableChild(this.increaseXOffset);
			this.addDrawableChild(this.decreaseYOffset);
			this.addDrawableChild(this.increaseYOffset);
			this.addDrawableChild(this.decreaseZOffset);
			this.addDrawableChild(this.increaseZOffset);
			this.addDrawableChild(this.decreasePitch);
			this.addDrawableChild(this.increasePitch);
			this.addDrawableChild(this.decreaseYaw);
			this.addDrawableChild(this.increaseYaw);
		}
	}
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if(this.client != null) {
			super.render(context, mouseX, mouseY, delta);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.scale_value", this.displayBlock.scale), 7, 7, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.x_offset_value", this.displayBlock.xOffset), 7, 37, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.y_offset_value", this.displayBlock.yOffset), 7, 67, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.z_offset_value", this.displayBlock.zOffset), 7, 97, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.pitch_value", MathHelper.floor(this.displayBlock.pitch*(180F/Math.PI))), 7, 127, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.yaw_value", MathHelper.floor(this.displayBlock.yaw*(180F/Math.PI))), 7, 157, 0xFFFFFFFF);
		}
	}


	private void editItemDisplayBlock() {
		C2SEditItemDisplayBlock.of(displayBlock).send();
	}
}
