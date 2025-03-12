package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditRecipeBlock;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RecipeBlockEditScreen extends GlowcaseScreen {
	private final RecipeBlockEntity recipeBlockEntity;

	private TextFieldWidget recipeWidget;
	private ButtonWidget zOffsetToggle;

	public RecipeBlockEditScreen(RecipeBlockEntity recipeBlockEntity) {
		this.recipeBlockEntity = recipeBlockEntity;
	}

	@Override
	public void init() {
		super.init();

		if (this.client == null) return;

		this.recipeWidget = new TextFieldWidget(this.client.textRenderer, width / 2 - 75, height / 2 - 55, 150, 20, Text.empty());
		this.recipeWidget.setMaxLength(1024);
		this.recipeWidget.setText(recipeBlockEntity.recipe);
		this.recipeWidget.setChangedListener(string -> {
			if (Identifier.tryParse(this.recipeWidget.getText()) != null) {
				this.recipeBlockEntity.recipe = this.recipeWidget.getText();
			}
		});

		this.zOffsetToggle = ButtonWidget.builder(Text.literal(this.recipeBlockEntity.zOffset.name()), action -> {
			switch (recipeBlockEntity.zOffset) {
				case FRONT -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.CENTER;
				case CENTER -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.BACK;
				case BACK -> recipeBlockEntity.zOffset = TextBlockEntity.ZOffset.FRONT;
			}

			this.zOffsetToggle.setMessage(Text.literal(this.recipeBlockEntity.zOffset.name()));
		}).dimensions(width / 2 - 75, height / 2 + 5, 150, 20).build();

		this.addDrawableChild(this.recipeWidget);
		this.addDrawableChild(this.zOffsetToggle);
	}

	@Override
	public void close() {
		recipeBlockEntity.setRecipe(recipeWidget.getText());
		C2SEditRecipeBlock.of(recipeBlockEntity).send();
		super.close();
	}
	
}
