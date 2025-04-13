package dev.hephaestus.glowcase.client.gui.screen.ingame;

import com.google.common.primitives.Floats;

import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditRecipeBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RecipeBlockEditScreen extends GlowcaseScreen {
	private final RecipeBlockEntity recipeBlockEntity;

	private TextFieldWidget recipeWidget;
	private TextFieldWidget rotationXWidget;
    private TextFieldWidget rotationYWidget; 

	private ButtonWidget zOffsetToggle;
	private int fontHeight = -1;

	public RecipeBlockEditScreen(RecipeBlockEntity recipeBlockEntity) {
		this.recipeBlockEntity = recipeBlockEntity;
	}

	@Override
	public void init() {
		super.init();

		if (this.client == null) return;

		if (fontHeight == -1) {
			fontHeight = this.client.textRenderer.fontHeight;
		}

		this.recipeWidget = new TextFieldWidget(this.client.textRenderer, width / 2 - 75, height / 2 - ((2 * fontHeight + 95) / 2) + fontHeight + 10, 150, 20, Text.empty());
		this.recipeWidget.setMaxLength(1024);
		this.recipeWidget.setText(recipeBlockEntity.recipe);
		this.recipeWidget.setChangedListener(string -> {
			if (Identifier.tryParse(this.recipeWidget.getText()) != null) {
				this.recipeBlockEntity.recipe = this.recipeWidget.getText();
			}
		});

		this.rotationXWidget = new TextFieldWidget(this.client.textRenderer, (width - 145) / 2, height / 2 - ((2 * fontHeight + 95) / 2) + 2 * fontHeight + 45, 70, 20, Text.empty());
		this.rotationXWidget.setMaxLength(1024);
		this.rotationXWidget.setText(Float.toString(recipeBlockEntity.rotationX));
		this.rotationXWidget.setChangedListener(s -> {
			if (Floats.tryParse(s) instanceof Float parsed) {
				recipeBlockEntity.rotationX = parsed;
			}
		});

		this.rotationYWidget = new TextFieldWidget(this.client.textRenderer, (width - 145) / 2 + 75, height / 2 - ((2 * fontHeight + 95) / 2) + 2 * fontHeight + 45, 70, 20, Text.empty());
		this.rotationYWidget.setMaxLength(1024);
		this.rotationYWidget.setText(Float.toString(recipeBlockEntity.rotationY));
		this.rotationYWidget.setChangedListener(s -> {
			if (Floats.tryParse(s) instanceof Float parsed) {
				recipeBlockEntity.rotationY = parsed;
			}
		});

		this.zOffsetToggle = ButtonWidget.builder(Text.literal(this.recipeBlockEntity.zOffset.name()), action -> {
			switch (recipeBlockEntity.zOffset) {
				case FRONT -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.CENTER;
				case CENTER -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.BACK;
				case BACK -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.FRONT;
			}

			this.zOffsetToggle.setMessage(Text.literal(this.recipeBlockEntity.zOffset.name()));
		}).dimensions(width / 2 - 75, height / 2 - ((2 * fontHeight + 95) / 2) + 2 * fontHeight + 75, 150, 20).build();

		this.addDrawableChild(this.recipeWidget);
		this.addDrawableChild(this.rotationXWidget);
        this.addDrawableChild(this.rotationYWidget);
		this.addDrawableChild(this.zOffsetToggle);
	}

	@Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.client == null) return;

		if (fontHeight == -1) {
			fontHeight = this.client.textRenderer.fontHeight;
		}

		context.drawTextWithShadow(
            this.client.textRenderer,
            Text.translatable("gui.glowcase.recipe"),
            width / 2 - (this.client.textRenderer.getWidth(Text.translatable("gui.glowcase.recipe")) / 2),
            height / 2 - ((2 * fontHeight + 95) / 2),
            0xFFFFFFFF
        );

		context.drawTextWithShadow(
            this.client.textRenderer,
            Text.translatable("gui.glowcase.pitch"),
            ((width - 145) / 2) + 35 - (this.client.textRenderer.getWidth(Text.translatable("gui.glowcase.pitch")) / 2),
            height / 2 - ((2 * fontHeight + 95) / 2) + fontHeight + 40,
            0xFFFFFFFF
        );

		context.drawTextWithShadow(
            this.client.textRenderer,
            Text.translatable("gui.glowcase.yaw"),
            ((width - 145) / 2) + 75 + 35 - (this.client.textRenderer.getWidth(Text.translatable("gui.glowcase.yaw")) / 2),
            height / 2 - ((2 * fontHeight + 95) / 2) + fontHeight + 40,
            0xFFFFFFFF
        );
	}

	@Override
	public void close() {
		recipeBlockEntity.setRecipe(recipeWidget.getText());
		C2SEditRecipeBlock.of(recipeBlockEntity).send();
		super.close();
	}
	
}
