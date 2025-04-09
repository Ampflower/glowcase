package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.SpriteBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import dev.hephaestus.glowcase.client.gui.widget.ingame.SuggestionListWidget;
import dev.hephaestus.glowcase.packet.C2SEditSpriteBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpriteBlockEditScreen extends GlowcaseScreen {
	private final SpriteBlockEntity spriteBlockEntity;

	private TextFieldWidget spriteWidget;
	private ButtonWidget spriteWidgetHelpButton;
	private ButtonWidget rotationWidget;
	private ButtonWidget zOffsetToggle;
	private TextFieldWidget colorEntryWidget;
	private TextFieldWidget scaleEntryWidget;

	private List<OrderedText> spriteHelpTooltipText;

	private SuggestionListWidget<String> suggestionWidget;
    private List<String> validSprites = new ArrayList<>();

	public SpriteBlockEditScreen(SpriteBlockEntity spriteBlockEntity) {
		this.spriteBlockEntity = spriteBlockEntity;
	}

	@Override
	public void init() {
		super.init();

		if (this.client == null) return;

		this.spriteWidget = new TextFieldWidget(this.client.textRenderer, width / 2 - 75, height / 2 - 55, 150, 20, Text.empty());
		this.spriteWidget.setMaxLength(255);
		this.spriteWidget.setText(spriteBlockEntity.getSprite());
		this.spriteWidget.setChangedListener(string -> {
			this.spriteBlockEntity.setSprite(this.spriteWidget.getText());
		});

		this.spriteWidgetHelpButton = ButtonWidget.builder(Text.literal("?"), action -> {})
			.dimensions(spriteWidget.getX() + spriteWidget.getWidth() + 4, spriteWidget.getY(),
				spriteWidget.getHeight(), spriteWidget.getHeight())
			.build();

		this.spriteHelpTooltipText = Tooltip.wrapLines(this.client, Text.translatable("gui.glowcase.screen.sprite_edit.sprite"));

		this.rotationWidget = ButtonWidget.builder(Text.translatable("gui.glowcase.rotate"), (action) -> {
			this.spriteBlockEntity.rotation = (this.spriteBlockEntity.rotation + 45) % 360;
		}).dimensions(width / 2 - 75, height / 2 - 25, 150, 20).build();

		this.zOffsetToggle = ButtonWidget.builder(Text.literal(this.spriteBlockEntity.zOffset.name()), action -> {
			switch (spriteBlockEntity.zOffset) {
				case FRONT -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.CENTER;
				case CENTER -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.BACK;
				case BACK -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.FRONT;
			}

			this.zOffsetToggle.setMessage(Text.literal(this.spriteBlockEntity.zOffset.name()));
		}).dimensions(width / 2 - 75, height / 2 + 5, 150, 20).build();

		this.colorEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 2 - 75, height / 2 + 35, 150, 20, Text.empty());
		this.colorEntryWidget.setText("#" + String.format("%1$06X", this.spriteBlockEntity.color & 0x00FFFFFF));
		this.colorEntryWidget.setChangedListener(string -> {
			TextColor.parse(this.colorEntryWidget.getText()).ifSuccess(color -> {
				this.spriteBlockEntity.color = color == null ? 0xFFFFFFFF : color.getRgb() | 0xFF000000;
			});
		});

		this.scaleEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 2 - 75, height / 2 + 65, 150, 20, Text.empty());
		this.scaleEntryWidget.setText(String.valueOf(this.spriteBlockEntity.scale));
		this.scaleEntryWidget.setChangedListener(string -> {
			 try {
				 this.spriteBlockEntity.scale = Float.parseFloat(string);
			 } catch (NumberFormatException ignored) {}
		});

		this.addDrawableChild(this.spriteWidget);
		this.addDrawableChild(this.spriteWidgetHelpButton);
		this.addDrawableChild(this.rotationWidget);
		this.addDrawableChild(this.zOffsetToggle);
		this.addDrawableChild(this.colorEntryWidget);
		this.addDrawableChild(this.scaleEntryWidget);

		List<String> registrySprites = Registries.ITEM.stream()
            .map(Registries.ITEM::getId)
            .map(Identifier::toString)
            .collect(Collectors.toList());
		
		List<String> resourceSprites = new ArrayList<>();
		ResourceManager resourceManager = this.client.getResourceManager();
		Map<Identifier, ?> spriteResourcesMap = resourceManager.findResources("textures/sprite", id -> id.getPath().endsWith(".png"));
		
		for (Identifier resId : spriteResourcesMap.keySet()) {
			String path = resId.getPath();

			if (path.startsWith("textures/sprite/") && path.endsWith(".png")) {
				String spriteName = path.substring("textures/sprite/".length(), path.length() - 4);
				resourceSprites.add(spriteName);
			}
		}

		Set<String> combinedSprites = new HashSet<>();
		combinedSprites.addAll(registrySprites);
		combinedSprites.addAll(resourceSprites);
		validSprites = new ArrayList<>(combinedSprites);

		suggestionWidget = new SuggestionListWidget<>(this.client.textRenderer, spriteWidget.getX(), spriteWidget.getY() + spriteWidget.getHeight() + 5, spriteWidget.getWidth(), 100, 10, 4, 5,
			(suggestion) -> spriteWidget.setText(suggestion), s -> s);
		
		spriteWidget.setChangedListener((text) -> {
			suggestionWidget.updateSuggestions(validSprites, text);
		});
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		// Tooltip is handled this way, since setting the tooltip directly on the help button widget causes the tooltip
		// to clip off-screen at higher GUI scales.
		if (this.spriteWidgetHelpButton.isHovered() || (this.spriteWidgetHelpButton.isFocused() && this.client.getNavigationType().isKeyboard())) {
			setTooltip(this.spriteHelpTooltipText);
		}

		// render the list over everything
		suggestionWidget.renderWidget(context, mouseX, mouseY, delta);
	}

	@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (suggestionWidget.isMouseOver(mouseX, mouseY) && spriteWidget.isFocused()) {
            return suggestionWidget.mouseClicked(mouseX, mouseY, button);
        } else {
            suggestionWidget.updateSuggestions(new ArrayList<>(), "");
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (suggestionWidget.draggingScrollbar) {
			if (suggestionWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
				return true;
		}
		
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (suggestionWidget.isMouseOver(mouseX, mouseY) && spriteWidget.isFocused()) {
            suggestionWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

	@Override
	public void close() {
		spriteBlockEntity.setSprite(spriteWidget.getText());
		spriteBlockEntity.markDirty();
		C2SEditSpriteBlock.of(spriteBlockEntity).send();
		super.close();
	}
}
