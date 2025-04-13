package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.SpriteBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import dev.hephaestus.glowcase.client.gui.widget.ingame.SuggestionListWidget;
import dev.hephaestus.glowcase.packet.C2SEditSpriteBlock;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;

public class SpriteBlockEditScreen extends GlowcaseScreen {
	private final SpriteBlockEntity spriteBlockEntity;

	private ButtonWidget spriteWidgetHelpButton;
	private ButtonWidget zOffsetToggle;

	private TextFieldWidget spriteWidget;
	private TextFieldWidget colorEntryWidget;
	private TextFieldWidget scaleEntryWidget;

	private TextFieldWidget offsetXWidget;
	private TextFieldWidget offsetYWidget;
	private TextFieldWidget offsetZWidget;
	private TextFieldWidget pitchWidget;
	private TextFieldWidget yawWidget;


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

		int fontHeight = this.client.textRenderer.fontHeight;
		int containerTop = (height - (3 * fontHeight + 125)) / 2;

		TextWidget offsetXLabel = new TextWidget(width / 10, containerTop, (((width - width / 5) - 10) / 3), 20, Text.translatable("gui.glowcase.x_offset_label"), this.client.textRenderer);
        TextWidget offsetYLabel = new TextWidget(width / 10 + (((width - width / 5) - 10) / 3) + 5, containerTop, (((width - width / 5) - 10) / 3), 20, Text.translatable("gui.glowcase.y_offset_label"), this.client.textRenderer);
        TextWidget offsetZLabel = new TextWidget(width / 10 + 2 * ((((width - width / 5) - 10) / 3) + 5), containerTop, (((width - width / 5) - 10) / 3), 20, Text.translatable("gui.glowcase.z_offset_label"), this.client.textRenderer);

		int offsetsFieldY = containerTop + fontHeight + 10;

		this.offsetXWidget = new TextFieldWidget(this.client.textRenderer, width / 10, offsetsFieldY, (((width - width / 5) - 10) / 3), 20, Text.empty());
        this.offsetXWidget.setText(Double.toString(spriteBlockEntity.offsetX));
		this.offsetXWidget.setChangedListener(s -> {
			if (Doubles.tryParse(s) instanceof Double parsed) {
				spriteBlockEntity.offsetX = parsed;
			}
		});

		this.offsetYWidget = new TextFieldWidget(this.client.textRenderer, width / 10 + (((width - width / 5) - 10) / 3) + 5, offsetsFieldY, (((width - width / 5) - 10) / 3), 20, Text.empty());
        this.offsetYWidget.setText(Double.toString(spriteBlockEntity.offsetY));
		this.offsetYWidget.setChangedListener(s -> {
			if (Doubles.tryParse(s) instanceof Double parsed) {
				spriteBlockEntity.offsetY = parsed;
			}
		});

		this.offsetZWidget = new TextFieldWidget(this.client.textRenderer, width / 10 + 2 * ((((width - width / 5) - 10) / 3) + 5), offsetsFieldY, (((width - width / 5) - 10) / 3), 20, Text.empty());
        this.offsetZWidget.setText(Double.toString(spriteBlockEntity.offsetZ));
		this.offsetZWidget.setChangedListener(s -> {
			if (Doubles.tryParse(s) instanceof Double parsed) {
				spriteBlockEntity.offsetZ = parsed;
			}
		});

		int spriteFieldY = containerTop + fontHeight + 30 + 5;
		int spriteFieldWidth = (width - width / 5) - 130;

		this.spriteWidget = new TextFieldWidget(this.client.textRenderer, width / 10, spriteFieldY, spriteFieldWidth, 20, Text.empty());
		this.spriteWidget.setMaxLength(255);
		this.spriteWidget.setText(spriteBlockEntity.getSprite());
		this.spriteWidget.setChangedListener(string -> {
			this.spriteBlockEntity.setSprite(this.spriteWidget.getText());
		});

		int pitchYawLabelY = spriteFieldY + 20 + 5;
		int pitchYawFieldWidth = ((width - width / 5) - 5) / 2;

        TextWidget pitchLabelWidget = new TextWidget(width / 10, pitchYawLabelY, pitchYawFieldWidth, 20, Text.translatable("gui.glowcase.pitch"), this.client.textRenderer);
        TextWidget yawLabelWidget = new TextWidget(width / 10 + pitchYawFieldWidth + 5, pitchYawLabelY, pitchYawFieldWidth, 20, Text.translatable("gui.glowcase.yaw"), this.client.textRenderer);

		int pitchYawFieldY = pitchYawLabelY + fontHeight + 10;
		this.pitchWidget = new TextFieldWidget(this.client.textRenderer, width / 10, pitchYawFieldY, pitchYawFieldWidth, 20, Text.empty());
		this.pitchWidget.setText(Float.toString(spriteBlockEntity.pitch));
		this.pitchWidget.setChangedListener(s -> {
			if (Floats.tryParse(s) instanceof Float parsed) {
				spriteBlockEntity.pitch = parsed;
			}
		});

		this.yawWidget = new TextFieldWidget(this.client.textRenderer, width / 10 + pitchYawFieldWidth + 5, pitchYawFieldY, pitchYawFieldWidth, 20, Text.empty());
		this.yawWidget.setText(Float.toString(spriteBlockEntity.yaw));
		this.yawWidget.setChangedListener(s -> {
			if (Floats.tryParse(s) instanceof Float parsed) {
				spriteBlockEntity.yaw = parsed;
			}
		});		

		this.spriteWidgetHelpButton = ButtonWidget.builder(Text.literal("?"), action -> {})
			.dimensions(this.spriteWidget.getX() + this.spriteWidget.getWidth() + 5, spriteFieldY, 20, 20)
			.build();

		this.spriteHelpTooltipText = Tooltip.wrapLines(this.client, Text.translatable("gui.glowcase.screen.sprite_edit.sprite"));

		this.zOffsetToggle = ButtonWidget.builder(Text.literal(this.spriteBlockEntity.zOffset.name()), action -> {
			switch (spriteBlockEntity.zOffset) {
				case FRONT -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.CENTER;
				case CENTER -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.BACK;
				case BACK -> spriteBlockEntity.zOffset = TextBlockEntity.ZOffset.FRONT;
			}

			this.zOffsetToggle.setMessage(Text.literal(this.spriteBlockEntity.zOffset.name()));
		}).dimensions(width / 10 + ((width - width / 5)) - 100, spriteFieldY, 100, 20).build();

        int scaleColorLabelY = pitchYawLabelY + fontHeight + 30 + 5;
		int halfWidth = (width - width / 5) / 2 - 2;

        TextWidget scaleLabelWidget = new TextWidget(width / 10, scaleColorLabelY, halfWidth, 20, Text.translatable("gui.glowcase.scale"), this.client.textRenderer);
        TextWidget colorLabelWidget = new TextWidget(width / 10 + halfWidth + 5, scaleColorLabelY, halfWidth, 20, Text.translatable("gui.glowcase.color"), this.client.textRenderer);

        int scaleColorFieldY = scaleColorLabelY + fontHeight + 10;
		this.colorEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10 + halfWidth + 5, scaleColorFieldY, halfWidth, 20, Text.empty());
		this.colorEntryWidget.setText("#" + String.format("%1$06X", this.spriteBlockEntity.color & 0x00FFFFFF));
		this.colorEntryWidget.setChangedListener(string -> {
			TextColor.parse(this.colorEntryWidget.getText()).ifSuccess(color -> {
				this.spriteBlockEntity.color = color == null ? 0xFFFFFFFF : color.getRgb() | 0xFF000000;
			});
		});

        this.scaleEntryWidget = new TextFieldWidget(this.client.textRenderer, width / 10, scaleColorFieldY, halfWidth, 20, Text.empty());
		this.scaleEntryWidget.setText(String.valueOf(this.spriteBlockEntity.scale));
		this.scaleEntryWidget.setChangedListener(s -> {
			if (Floats.tryParse(s) instanceof Float parsed) {
				spriteBlockEntity.scale = parsed;
			}
		});

        this.addDrawableChild(offsetXLabel);
        this.addDrawableChild(offsetYLabel);
        this.addDrawableChild(offsetZLabel);
        this.addDrawableChild(offsetXWidget);
        this.addDrawableChild(offsetYWidget);
        this.addDrawableChild(offsetZWidget);
        this.addDrawableChild(spriteWidget);
        this.addDrawableChild(spriteWidgetHelpButton);
        this.addDrawableChild(zOffsetToggle);
        this.addDrawableChild(pitchLabelWidget);
        this.addDrawableChild(yawLabelWidget);
        this.addDrawableChild(pitchWidget);
        this.addDrawableChild(yawWidget);
        this.addDrawableChild(scaleLabelWidget);
        this.addDrawableChild(colorLabelWidget);
        this.addDrawableChild(scaleEntryWidget);
        this.addDrawableChild(colorEntryWidget);

		ResourceManager resourceManager = this.client.getResourceManager();
		validSprites = allValidSprites(resourceManager);

		suggestionWidget = new SuggestionListWidget<>(this.client.textRenderer, spriteWidget.getX(), spriteWidget.getY() + spriteWidget.getHeight() + 5, spriteWidget.getWidth(), 100, 10, 4, 5,
			(suggestion) -> spriteWidget.setText(suggestion), s -> s);
		
		spriteWidget.setChangedListener((text) -> {
			suggestionWidget.updateSuggestions(validSprites, text);
		});
	}

	/**
	 * A list of all valid entries for {@link #spriteWidget}. Used for suggestions.
	 */
	public static List<String> allValidSprites(ResourceManager resourceManager) {
		var validSprites = new ArrayList<String>();

		// Add all sprites inside /textures/sprite, these are explicitly meant for the sprite block
		// and can be used with just their filename. As these are intended to be used here, we'll list them first
		resourceManager.findResources("textures/sprite", id -> id.getPath().endsWith(".png")).forEach((sprite, res) -> {
			validSprites.add(sprite.getPath().substring("textures/sprite/".length(), sprite.getPath().length() - 4));
		});

		// You can use any texture. Technically I think you can also use ones outside of texture
		// But findResources requires us to filter
		resourceManager.findResources("textures", id -> id.getPath().endsWith(".png")).forEach((sprite, res) -> {
			validSprites.add(sprite.toString());
		});

		// You can also display any item
		Registries.ITEM.stream()
			.map(Registries.ITEM::getId)
			.map(Identifier::toString)
			.forEach(validSprites::add);

		// And you can use any modid to display its icon
		FabricLoader.getInstance().getAllMods().forEach(mod -> {
			validSprites.add("mod:"+mod.getMetadata().getId());
		});

		return validSprites;
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
