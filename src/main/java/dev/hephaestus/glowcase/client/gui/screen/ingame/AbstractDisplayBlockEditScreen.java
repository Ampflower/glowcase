package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.AbstractDisplayBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public abstract class AbstractDisplayBlockEditScreen extends GlowcaseScreen {
	protected final AbstractDisplayBlockEntity displayBlock;
	protected ButtonWidget decreaseSize;
	protected ButtonWidget increaseSize;

	protected ButtonWidget decreaseXOffset;
	protected ButtonWidget increaseXOffset;
	protected ButtonWidget decreaseYOffset;
	protected ButtonWidget increaseYOffset;
	protected ButtonWidget decreaseZOffset;
	protected ButtonWidget increaseZOffset;
	protected ButtonWidget decreasePitch;
	protected ButtonWidget increasePitch;
	protected ButtonWidget decreaseYaw;
	protected ButtonWidget increaseYaw;

	private final float pitchYawChange = 15F * ((float) Math.PI / 180F);

	public AbstractDisplayBlockEditScreen(AbstractDisplayBlockEntity displayBlock) {
		this.displayBlock = displayBlock;
	}

	@Override
	public void init() {
		super.init();

		if (this.client != null) {
			this.decreaseSize = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getScale().sub(0.125F, 0.125F, 0.125F);
				clampValues();
				editDisplayBlock();
			}).dimensions(90, 0, 20, 20).build();

			this.increaseSize = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getScale().add(0.125F, 0.125F, 0.125F);
				clampValues();
				editDisplayBlock();
			}).dimensions(110, 0, 20, 20).build();

			this.decreaseXOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(0.125F, 0, 0);
				editDisplayBlock();
			}).dimensions(90, 30, 20, 20).build();

			this.increaseXOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(0.125F, 0, 0);
				editDisplayBlock();
			}).dimensions(110, 30, 20, 20).build();

			this.decreaseYOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(0, 0, 0.125F);
				editDisplayBlock();
			}).dimensions(90, 60, 20, 20).build();

			this.increaseYOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(0, 0.125F, 0);
				editDisplayBlock();
			}).dimensions(110, 60, 20, 20).build();

			this.decreaseZOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(0, 0, 0.125F);
				editDisplayBlock();
			}).dimensions(90, 90, 20, 20).build();

			this.increaseZOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(0, 0, 0.125F);
				editDisplayBlock();
			}).dimensions(110, 90, 20, 20).build();

			this.decreasePitch = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.setPitch(this.displayBlock.getPitch() - Math.max(0, pitchYawChange));
				editDisplayBlock();
			}).dimensions(90, 120, 20, 20).build();

			this.increasePitch = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.setPitch(this.displayBlock.getPitch() + pitchYawChange);
				editDisplayBlock();
			}).dimensions(110, 120, 20, 20).build();

			this.decreaseYaw = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.setYaw(this.displayBlock.getYaw() - Math.max(0, pitchYawChange));
				editDisplayBlock();
			}).dimensions(90, 150, 20, 20).build();

			this.increaseYaw = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.setYaw(this.displayBlock.getYaw() + pitchYawChange);
				editDisplayBlock();
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

	public void clampValues() {
		this.displayBlock.setScale(new Vector3f(
			MathHelper.clamp(this.displayBlock.getScale().x(), -10F, 10F),
			MathHelper.clamp(this.displayBlock.getScale().y(), -10F, 10F),
			MathHelper.clamp(this.displayBlock.getScale().z(), -10F, 10F)
		));
		this.displayBlock.setOffset(new Vector3f(
			MathHelper.clamp(this.displayBlock.getOffset().x(), -5F, 5F),
			MathHelper.clamp(this.displayBlock.getOffset().y(), -5F, 5F),
			MathHelper.clamp(this.displayBlock.getOffset().z(), -5F, 5F)
		));
		this.displayBlock.setPitch(MathHelper.clamp(this.displayBlock.getPitch(), -6.28319F, 6.28319F));
		this.displayBlock.setYaw(MathHelper.clamp(this.displayBlock.getYaw(), -6.28319F, 6.28319F));
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.client != null) {
			super.render(context, mouseX, mouseY, delta);

			int degreesPitch = (int) (15 * Math.round((this.displayBlock.getPitch() * (180F / Math.PI) / 15)));
			int degreesYaw = (int) (15 * Math.round((this.displayBlock.getYaw() * (180F / Math.PI) / 15)));

			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.scale_value", this.displayBlock.getScale().x()), 7, 7, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.x_offset_value", this.displayBlock.getOffset().x()), 7, 37, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.y_offset_value", this.displayBlock.getOffset().y()), 7, 67, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.z_offset_value", this.displayBlock.getOffset().z()), 7, 97, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.pitch_value", degreesPitch), 7, 127, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.yaw_value", degreesYaw), 7, 157, 0xFFFFFFFF);
		}
	}

	protected void editDisplayBlock() {
		clampValues();
	}
}
