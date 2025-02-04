package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ScreenBlockEditScreen extends GlowcaseScreen {
	private final ScreenBlockEntity screenBlockEntity;

	private TextFieldWidget urlEntryWidget;
	private TextFieldWidget altEntryWidget;

	public ScreenBlockEditScreen(ScreenBlockEntity screenBlockEntity) {
		this.screenBlockEntity = screenBlockEntity;
	}

	@Override
	protected void init() {
		super.init();

		if (this.client == null) return;

		this.urlEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10, height / 2 - 30, 8 * width / 10, 20, Text.empty());
		this.urlEntryWidget.setText(this.screenBlockEntity.url);
		this.urlEntryWidget.setPlaceholder(Text.translatable("gui.glowcase.url"));


		this.addDrawableChild(this.urlEntryWidget);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.close();
			return true;
		} else if (this.urlEntryWidget.isActive()) {
			return this.urlEntryWidget.keyPressed(keyCode, scanCode, modifiers);
		} else {
			return false;
		}
	}
}
