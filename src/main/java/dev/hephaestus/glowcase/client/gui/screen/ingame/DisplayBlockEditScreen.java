package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.DisplayBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import com.google.common.primitives.Floats;

public abstract class DisplayBlockEditScreen extends GlowcaseScreen {
	protected final DisplayBlockEntity displayBlock;

	protected TextFieldWidget scaleField;
    protected TextFieldWidget xOffsetField;
    protected TextFieldWidget yOffsetField;
    protected TextFieldWidget zOffsetField;
    protected TextFieldWidget pitchField;
    protected TextFieldWidget yawField;

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

	private final float pitchYawChange = 15F;
	private final float scaleOffsetChange = 0.125F;

	public DisplayBlockEditScreen(DisplayBlockEntity displayBlock) {
		this.displayBlock = displayBlock;
	}

	@Override
	public void init() {
		super.init();

		if (this.client != null) {
			this.scaleField = new TextFieldWidget(this.client.textRenderer, 90, 10, 60, 20, Text.empty());
            this.scaleField.setText(String.valueOf(this.displayBlock.getScale().x()));
			this.scaleField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					this.displayBlock.setScale(new Vector3f(parsed, parsed, parsed));
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreaseSize = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getScale().sub(scaleOffsetChange, scaleOffsetChange, scaleOffsetChange);
				clampValues();
				editDisplayBlock();
				this.scaleField.setText(String.valueOf(this.displayBlock.getScale().x()));
			}).dimensions(90 + 60 + 5, 10, 20, 20).build();

			this.increaseSize = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getScale().add(scaleOffsetChange, scaleOffsetChange, scaleOffsetChange);
				clampValues();
				editDisplayBlock();
				this.scaleField.setText(String.valueOf(this.displayBlock.getScale().x()));
			}).dimensions(90 + 60 + 5 + 20, 10, 20, 20).build();

			this.xOffsetField = new TextFieldWidget(this.client.textRenderer, 90, 40, 60, 20, Text.empty());
            this.xOffsetField.setText(String.valueOf(this.displayBlock.getOffset().x()));
			this.xOffsetField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					Vector3f offset = this.displayBlock.getOffset();
					offset.x = parsed;
					this.displayBlock.setOffset(offset);
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreaseXOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(scaleOffsetChange, 0, 0);
				editDisplayBlock();
				this.xOffsetField.setText(String.valueOf(this.displayBlock.getOffset().x()));
			}).dimensions(90 + 60 + 5, 40, 20, 20).build();

			this.increaseXOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(scaleOffsetChange, 0, 0);
				editDisplayBlock();
				this.xOffsetField.setText(String.valueOf(this.displayBlock.getOffset().x()));
			}).dimensions(90 + 60 + 5 + 20, 40, 20, 20).build();

			this.yOffsetField = new TextFieldWidget(this.client.textRenderer, 90, 70, 60, 20, Text.empty());
            this.yOffsetField.setText(String.valueOf(this.displayBlock.getOffset().y()));
			this.yOffsetField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					Vector3f offset = this.displayBlock.getOffset();
					offset.y = parsed;
					this.displayBlock.setOffset(offset);
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreaseYOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(0, scaleOffsetChange, 0);
				editDisplayBlock();
				this.yOffsetField.setText(String.valueOf(this.displayBlock.getOffset().y()));
			}).dimensions(90 + 60 + 5, 70, 20, 20).build();

			this.increaseYOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(0, scaleOffsetChange, 0);
				editDisplayBlock();
				this.yOffsetField.setText(String.valueOf(this.displayBlock.getOffset().y()));
			}).dimensions(90 + 60 + 5 + 20, 70, 20, 20).build();

			this.zOffsetField = new TextFieldWidget(this.client.textRenderer, 90, 100, 60, 20, Text.empty());
            this.zOffsetField.setText(String.valueOf(this.displayBlock.getOffset().z()));
			this.zOffsetField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					Vector3f offset = this.displayBlock.getOffset();
					offset.z = parsed;
					this.displayBlock.setOffset(offset);
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreaseZOffset = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.getOffset().sub(0, 0, scaleOffsetChange);
				editDisplayBlock();
				this.zOffsetField.setText(String.valueOf(this.displayBlock.getOffset().z()));
			}).dimensions(90 + 60 + 5, 100, 20, 20).build();

			this.increaseZOffset = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.getOffset().add(0, 0, scaleOffsetChange);
				editDisplayBlock();
				this.zOffsetField.setText(String.valueOf(this.displayBlock.getOffset().z()));
			}).dimensions(90 + 60 + 5 + 20, 100, 20, 20).build();

			this.pitchField = new TextFieldWidget(this.client.textRenderer, 90, 130, 60, 20, Text.empty());
            this.pitchField.setText(String.valueOf((int)this.displayBlock.getPitch()));
			this.pitchField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					this.displayBlock.setPitch(Math.round(parsed));
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreasePitch = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.setPitch(this.displayBlock.getPitch() - pitchYawChange);
				editDisplayBlock();
				this.pitchField.setText(String.valueOf((int)this.displayBlock.getPitch()));
			}).dimensions(90 + 60 + 5, 130, 20, 20).build();

			this.increasePitch = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.setPitch(this.displayBlock.getPitch() + pitchYawChange);
				editDisplayBlock();
				this.pitchField.setText(String.valueOf((int)this.displayBlock.getPitch()));
			}).dimensions(90 + 60 + 5 + 20, 130, 20, 20).build();

			this.yawField = new TextFieldWidget(this.client.textRenderer, 90, 160, 60, 20, Text.empty());
            this.yawField.setText(String.valueOf((int)this.displayBlock.getYaw()));
			this.yawField.setChangedListener(string -> {
				if (Floats.tryParse(string) instanceof Float parsed) {
					this.displayBlock.setYaw(Math.round(parsed));
					clampValues();
					editDisplayBlock();
				}
			});

			this.decreaseYaw = ButtonWidget.builder(Text.literal("-"), action -> {
				this.displayBlock.setYaw(this.displayBlock.getYaw() - pitchYawChange);
				editDisplayBlock();
				this.yawField.setText(String.valueOf((int)this.displayBlock.getYaw()));
			}).dimensions(90 + 60 + 5, 160, 20, 20).build();

			this.increaseYaw = ButtonWidget.builder(Text.literal("+"), action -> {
				this.displayBlock.setYaw(this.displayBlock.getYaw() + pitchYawChange);
				editDisplayBlock();
				this.yawField.setText(String.valueOf((int)this.displayBlock.getYaw()));
			}).dimensions(90 + 60 + 5 + 20, 160, 20, 20).build();

			this.addDrawableChild(this.scaleField);
			this.addDrawableChild(this.xOffsetField);
			this.addDrawableChild(this.yOffsetField);
			this.addDrawableChild(this.zOffsetField);
			this.addDrawableChild(this.pitchField);
			this.addDrawableChild(this.yawField);
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
			MathHelper.clamp(Math.round(this.displayBlock.getScale().x() / scaleOffsetChange) * scaleOffsetChange, -10F, 10F),
			MathHelper.clamp(Math.round(this.displayBlock.getScale().y() / scaleOffsetChange) * scaleOffsetChange, -10F, 10F),
			MathHelper.clamp(Math.round(this.displayBlock.getScale().z() / scaleOffsetChange) * scaleOffsetChange, -10F, 10F)
		));
		this.displayBlock.setOffset(new Vector3f(
			MathHelper.clamp(Math.round(this.displayBlock.getOffset().x() / scaleOffsetChange) * scaleOffsetChange, -5F, 5F),
			MathHelper.clamp(Math.round(this.displayBlock.getOffset().y() / scaleOffsetChange) * scaleOffsetChange, -5F, 5F),
			MathHelper.clamp(Math.round(this.displayBlock.getOffset().z() / scaleOffsetChange) * scaleOffsetChange, -5F, 5F)
		));
		this.displayBlock.setPitch(Math.round(((this.displayBlock.getPitch() + 360) % 360) / 15) * 15);
		this.displayBlock.setYaw(Math.round(((this.displayBlock.getYaw() + 360) % 360) / 15) * 15);
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.client != null) {
			super.render(context, mouseX, mouseY, delta);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.scale_label"), 20, 17, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.x_offset_label"), 20, 47, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.y_offset_label"), 20, 77, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.z_offset_label"), 20, 107, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.pitch_value"), 20, 137, 0xFFFFFFFF);
			context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.glowcase.yaw_value"), 20, 167, 0xFFFFFFFF);
		}
	}

	protected void editDisplayBlock() {
		clampValues();
	}
}
