package dev.hephaestus.glowcase.client.gui.screen.ingame;

import com.google.common.primitives.Floats;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditScreenBlock;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ScreenBlockEditScreen extends GlowcaseScreen {
	private final ScreenBlockEntity screenBlockEntity;

	private TextFieldWidget widthEntryWidget;
	private TextFieldWidget heightEntryWidget;
	private ButtonWidget zOffsetToggle;

	private CheckboxWidget einkCheckWidget;
	private CheckboxWidget stretchCheckWidget;

	private TextFieldWidget urlEntryWidget;
	private TextFieldWidget altEntryWidget;

	public ScreenBlockEditScreen(ScreenBlockEntity screenBlockEntity) {
		this.screenBlockEntity = screenBlockEntity;
	}

	@Override
	protected void init() {
		super.init();
		if (this.client == null) return;

		this.widthEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10, height / 2 - 70, 2 * width / 10, 20, Text.empty());
		this.widthEntryWidget.setText(""+this.screenBlockEntity.width);
		this.widthEntryWidget.setPlaceholder(Text.translatable("gui.glowcase.width"));
		this.widthEntryWidget.setChangedListener(string -> {
			if (Floats.tryParse(string) instanceof Float parsed)
				screenBlockEntity.width = parsed;
		});

		MutableText timesLiteral = Text.literal("×");
		TextWidget timesLabel = new TextWidget(3 * width / 10 + 5, height / 2 - 70, textRenderer.getWidth(timesLiteral), 20, timesLiteral, this.client.textRenderer);

		this.heightEntryWidget = new TextFieldWidget(this.client.textRenderer, 3 * width / 10 + 10 + textRenderer.getWidth(timesLiteral), height / 2 - 70, 2 * width / 10, 20, Text.empty());
		this.heightEntryWidget.setText(""+this.screenBlockEntity.height);
		this.heightEntryWidget.setPlaceholder(Text.translatable("gui.glowcase.height"));
		this.heightEntryWidget.setChangedListener(string -> {
			if (Floats.tryParse(string) instanceof Float parsed)
				screenBlockEntity.height = parsed;
		});

		this.zOffsetToggle = ButtonWidget.builder(Text.literal(this.screenBlockEntity.zOffset.name()), action -> {
			switch (screenBlockEntity.zOffset) {
				case FRONT -> screenBlockEntity.zOffset = ScreenBlockEntity.ZOffset.CENTER;
				case CENTER -> screenBlockEntity.zOffset = ScreenBlockEntity.ZOffset.BACK;
				case BACK -> screenBlockEntity.zOffset = ScreenBlockEntity.ZOffset.FRONT;
			}
			this.zOffsetToggle.setMessage(Text.literal(this.screenBlockEntity.zOffset.name()));
		}).dimensions(7 * width / 10, height / 2 - 70, 2 * width / 10, 20).build();

		this.einkCheckWidget = CheckboxWidget.builder(Text.translatable("gui.glowcase.screen.eink"), this.client.textRenderer)
			.checked(this.screenBlockEntity.eink)
			.callback((checkbox, checked) -> this.screenBlockEntity.eink = checked)
			.pos(width / 10, height / 2 - 35)
			.build();

		this.stretchCheckWidget = CheckboxWidget.builder(Text.translatable("gui.glowcase.screen.stretch"), this.client.textRenderer)
			.checked(this.screenBlockEntity.stretch)
			.callback((checkbox, checked) -> this.screenBlockEntity.stretch = checked)
			.pos(width / 10, height / 2 - 10)
			.build();

		this.urlEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10, height / 2 + 20, 8 * width / 10, 20, Text.empty());
		this.urlEntryWidget.setMaxLength(ScreenBlockEntity.URL_MAX_LENGTH);
		this.urlEntryWidget.setText(this.screenBlockEntity.url);
		this.urlEntryWidget.setPlaceholder(Text.translatable("gui.glowcase.url"));
		// We don't change the url on the fly here as that would cause many fetch requests which we don't want

		this.altEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10, height / 2 + 40 + 5, 8 * width / 10, 40, Text.empty());
		this.altEntryWidget.setMaxLength(ScreenBlockEntity.ALT_MAX_LENGTH);
		this.altEntryWidget.setText(this.screenBlockEntity.alt);
		this.altEntryWidget.setPlaceholder(Text.translatable("gui.glowcase.alt"));
		this.altEntryWidget.setChangedListener(string -> screenBlockEntity.alt = string);

		this.addDrawableChild(this.widthEntryWidget);
		this.addDrawableChild(timesLabel);
		this.addDrawableChild(this.heightEntryWidget);
		this.addDrawableChild(this.zOffsetToggle);

		this.addDrawableChild(this.einkCheckWidget);
		this.addDrawableChild(this.stretchCheckWidget);

		this.addDrawableChild(this.urlEntryWidget);
		this.addDrawableChild(this.altEntryWidget);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.close();
			return true;
		} else if (this.widthEntryWidget.isActive()) {
			return this.widthEntryWidget.keyPressed(keyCode, scanCode, modifiers);
		} else if (this.heightEntryWidget.isActive()) {
			return this.heightEntryWidget.keyPressed(keyCode, scanCode, modifiers);
		} else if (this.urlEntryWidget.isActive()) {
			return this.urlEntryWidget.keyPressed(keyCode, scanCode, modifiers);
		} else if (this.altEntryWidget.isActive()) {
			return this.altEntryWidget.keyPressed(keyCode, scanCode, modifiers);
		} else {
			return false;
		}
	}

	@Override
	public void close() {
		screenBlockEntity.eink = einkCheckWidget.isChecked();
		screenBlockEntity.stretch = stretchCheckWidget.isChecked();
		screenBlockEntity.setImage(
			urlEntryWidget.getText(),
			altEntryWidget.getText(),
			null
		);

		C2SEditScreenBlock.of(screenBlockEntity).send();
		super.close();
	}
}
