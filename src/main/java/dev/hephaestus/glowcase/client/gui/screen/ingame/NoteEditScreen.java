package dev.hephaestus.glowcase.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.gui.widget.ingame.ColorPickerWidget;
import dev.hephaestus.glowcase.item.component.NoteComponent;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class NoteEditScreen extends TextEditorScreen {
	private static final Identifier TEXTURE = Glowcase.id("textures/gui/note.png");
	private static final int TXT_COLOR = 0x000000;

	private static final int BG_SIZE = 256;

	private static final int BG_WIDTH = 244 ;
	private static final int BG_HEIGHT = 117;

	private static final int TXT_OFF_Y = 12;
	private static final int TXT_X_PADDING = 15 * 2;

	private final List<Text> lines;
	private NoteComponent.Alignment textAlignment;

	public static final NodeParser PARSER = TagParser.DEFAULT;
	private SelectionManager selectionManager;
	private int currentRow;
	private long ticksSinceOpened = 0;

	private boolean signing = false;
	private List<StringVisitable> signing_text;

	private ColorPickerWidget colorPickerWidget;
	private ButtonWidget doneButton;
	private ButtonWidget signButton;
	private ButtonWidget changeAlignment;

	public NoteEditScreen(ItemStack stack) {
		if (stack.contains(Glowcase.NOTE_COMPONENT.get())) {
			// Load data
			NoteComponent note = stack.get(Glowcase.NOTE_COMPONENT.get());
			assert note != null;

			lines = note.lines();
			textAlignment =  note.alignment();
		} else {
			// Default values
			lines = new ArrayList<>();

			for (int i=0; i<NoteComponent.LINES_LIMIT; i++)
				lines.add(Text.literal(""));

			textAlignment = NoteComponent.Alignment.LEFT;
		}
	}

	@Override
	protected void init() {
		super.init();
		if (client == null) return;

		selectionManager = new SelectionManager(
			() -> getRawLine(currentRow),
			(string) -> setRawLine(currentRow, string),
			SelectionManager.makeClipboardGetter(client),
			SelectionManager.makeClipboardSetter(client),
			(string) -> true);

		// Setup Signing Screen

		signing_text = new ArrayList<>();
		//noinspection unchecked
		Pair<Integer, Text>[] lines = new Pair[]{
			new Pair<>(2, Text.translatable("gui.glowcase.note.signing")),
			new Pair<>(3, Text.translatable("gui.glowcase.note.warning")),
			new Pair<>(1, Text.literal("")),
			new Pair<>(1, Text.translatable("gui.glowcase.note.title")),
			new Pair<>(1, Text.translatable("gui.glowcase.note.author")),
			new Pair<>(1, Text.literal("")),
			new Pair<>(1, Text.translatable("gui.glowcase.note.required").setStyle(Style.EMPTY.withColor(Formatting.RED))),
		};
		for (Pair<Integer, Text> section : lines) {
			int height = section.getFirst();
			List<StringVisitable> texts = textRenderer.getTextHandler().wrapLines(section.getSecond(), BG_WIDTH - TXT_X_PADDING, Style.EMPTY);

			for (int i=0; i<height; i++) {
				if (i+1 <= texts.size()) {
					StringVisitable text = texts.get(i);
					if (i == (height - 1) && texts.size() > height)
						text = ensureBounds(textRenderer, text);

					signing_text.add(text);
				} else {
					signing_text.add(Text.empty());
				}
			}
		}

		// Widgets
		int offset = 7;

		this.changeAlignment = ButtonWidget.builder(Text.stringifiedTranslatable("gui.glowcase.alignment", textAlignment), action -> {
			switch (textAlignment) {
				case LEFT -> textAlignment = NoteComponent.Alignment.CENTER;
				case CENTER -> textAlignment = NoteComponent.Alignment.RIGHT;
				case RIGHT -> textAlignment = NoteComponent.Alignment.LEFT;
			}

			this.changeAlignment.setMessage(Text.stringifiedTranslatable("gui.glowcase.alignment", textAlignment));
		}).dimensions(width/2 - BG_WIDTH/2, height/2 - BG_HEIGHT/2 - offset - 20, BG_WIDTH/12 * 6 - 3 - 7, 20).build();

		signButton = ButtonWidget.builder(Text.translatable("book.signButton"), action -> {
			if (!signing) {
				signing = true;
				doneButton.setMessage(Text.translatable("gui.cancel"));
				signButton.setMessage(Text.translatable("book.finalizeButton"));
				signButton.active = false;
			} else {
				// TODO: Sync with title and author
				close();
			}
		}).dimensions(width/2 - BG_WIDTH/2, height/2 + BG_HEIGHT/2 + offset, BG_WIDTH/2-3, 20).build();
		doneButton = ButtonWidget.builder(Text.translatable("gui.done"), action -> {
			close();
			// TODO: When in sign mode, go to previous mode instead of closing
		}).dimensions(width/2 + BG_WIDTH/2 - (BG_WIDTH/2-3), height/2 + BG_HEIGHT/2 + offset, BG_WIDTH/2-3, 20).build();


		this.colorPickerWidget = ColorPickerWidget.builder(this,216, 10).size(182, 104).build();
		this.colorPickerWidget.toggle(false); //start deactivated

		this.addDrawableChild(colorPickerWidget);

		addDrawableChild(changeAlignment);
		addDrawableChild(doneButton);
		addDrawableChild(signButton);

		addFormattingButtons(width/2 - BG_WIDTH/2 + BG_WIDTH/12 * 6 - 5 - 7, height/2 - BG_HEIGHT/2 - offset - 20 - 4, width / 100, 20, 2);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (client == null)
			return;

		super.render(context, mouseX, mouseY, delta);

		List<? extends StringVisitable> screen = signing ? signing_text : lines;
		NoteComponent.Alignment alignment = signing ? NoteComponent.Alignment.LEFT : textAlignment;

		// Text rendering
		for (int i=0; i<screen.size(); i++) {
			StringVisitable text = screen.get(i);

			// TODO: Note! Due to the formatting possibilities, for selected lines,
			//  this can go way out of bounds. Scrolling needs to be added for the selected line
			if (!isInBounds(textRenderer, text))
				text = ensureBounds(textRenderer, text);

			int line_width = textRenderer.getWidth(text);
			float x = switch (alignment) {
				case LEFT -> width/2f - BG_WIDTH/2f + TXT_X_PADDING/2f;
				case CENTER -> width/2f - line_width/2f;
				case RIGHT ->  width/2f + BG_WIDTH/2f - TXT_X_PADDING/2f - line_width;
			};

			if (i == currentRow)
				text = Text.literal(getRawLine(currentRow));
			context.drawText(textRenderer, Language.getInstance().reorder(text), (int) x, (height/2 - BG_HEIGHT/2 + TXT_OFF_Y) + (textRenderer.fontHeight * i), TXT_COLOR, false);
		}

		// Cursor / Selection
		// I literally copied this from TextBlockEditScreen, we might want to abstract this down to a shared abstract class.
		int caretStart = selectionManager.getSelectionStart();
		int caretEnd = selectionManager.getSelectionEnd();

		// TODO: For some reason, the cursor is moving by one pixel for CENTERED aligned text and two pixels for RIGHT aligned text

		if (caretStart >= 0) {
			String line = getRawLine(currentRow);
			int selectionStart = MathHelper.clamp(Math.min(caretStart, caretEnd), 0, line.length());
			int selectionEnd = MathHelper.clamp(Math.max(caretStart, caretEnd), 0, line.length());

			String preSelection = line.substring(0, MathHelper.clamp(line.length(), 0, selectionStart));
			int startX = client.textRenderer.getWidth(preSelection);
			int startY = (height/2 - BG_HEIGHT/2 + TXT_OFF_Y) + (textRenderer.fontHeight * currentRow);

			float push = switch (alignment) {
				case LEFT -> width/2f - BG_WIDTH/2f + TXT_X_PADDING/2f;
				case CENTER -> width/2f - textRenderer.getWidth(line)/2f;
				case RIGHT ->  width/2f + BG_WIDTH/2f - TXT_X_PADDING/2f - textRenderer.getWidth(line);
			};

			startX += (int) push;

			int caretLength = 9;
			if (this.ticksSinceOpened / 6 % 2 == 0 /*&& !this.colorEntryWidget.isActive()*/) {
				if (selectionStart < line.length()) {
					context.fill(startX, startY, startX + 1, startY + caretLength, 0xCC000000);
				} else {
					context.drawText(client.textRenderer, "_", startX, startY, TXT_COLOR, false);
				}
			}

			if (caretStart != caretEnd) {
				int endX = startX + this.client.textRenderer.getWidth(line.substring(selectionStart, selectionEnd));
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				RenderSystem.enableColorLogicOp();
				RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
				bufferBuilder.vertex(context.getMatrices().peek().getPositionMatrix(), startX, startY + caretLength, 0.0F).color(0, 0, 255, 255);
				bufferBuilder.vertex(context.getMatrices().peek().getPositionMatrix(), endX, startY + caretLength, 0.0F).color(0, 0, 255, 255);
				bufferBuilder.vertex(context.getMatrices().peek().getPositionMatrix(), endX, startY, 0.0F).color(0, 0, 255, 255);
				bufferBuilder.vertex(context.getMatrices().peek().getPositionMatrix(), startX, startY, 0.0F).color(0, 0, 255, 255);
				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
				RenderSystem.disableColorLogicOp();
			}
		}
	}

	@Override
	public void tick() {
		++this.ticksSinceOpened;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		context.drawTexture(TEXTURE, width/2 - BG_WIDTH/2, height/2 - BG_HEIGHT/2, BG_WIDTH, BG_HEIGHT, 0, 0, BG_WIDTH, BG_HEIGHT, BG_SIZE, BG_SIZE);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.colorPickerWidget.active && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE)) {
			if(keyCode == GLFW.GLFW_KEY_ENTER) {
				this.colorPickerWidget.confirmColor();
			} else {
				this.colorPickerWidget.cancel();
			}
			return true;
		} else {
			setFocused(null);
			if (keyCode == GLFW.GLFW_KEY_UP) {
				currentRow = Math.max(currentRow - 1, 0);
				selectionManager.putCursorAtEnd();
				return true;
			} else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
				currentRow = Math.min(currentRow + 1, NoteComponent.LINES_LIMIT);
				selectionManager.putCursorAtEnd();
				return true;
			} else {
				return selectionManager.handleSpecialKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
			}
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (isInBounds(textRenderer, lines.get(currentRow))) {
			this.selectionManager.insert(chr);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(colorPickerWidget.active && colorPickerWidget.visible) {
			if(colorPickerWidget.isMouseOver(mouseX, mouseY)) {
				colorPickerWidget.mouseClicked(mouseX, mouseY, button);
				this.setFocused(colorPickerWidget);
				this.setDragging(true);
				return true;
			} else {
				if(!this.colorPickerWidget.targetElement.isMouseOver(mouseX, mouseY)) {
					toggleColorPicker(false);
				}
			}
		}

		// TODO: Mouse navigation in editor
		/*if (mouseY > topOffset) {
			this.currentRow = MathHelper.clamp((int) (mouseY - topOffset) / 12, 0, this.textBlockEntity.lines.size() - 1);
			this.setFocused(null);
			String baseContents = this.textBlockEntity.getRawLine(currentRow);
			int baseContentsWidth = this.textRenderer.getWidth(baseContents);
			int contentsStart;
			int contentsEnd;
			switch (this.textBlockEntity.textAlignment) {
				case LEFT -> {
					contentsStart = this.width / 10;
					contentsEnd = contentsStart + baseContentsWidth;
				}
				case CENTER -> {
					int midpoint = this.width / 2;
					int textMidpoint = baseContentsWidth / 2;
					contentsStart = midpoint - textMidpoint;
					contentsEnd = midpoint + textMidpoint;
				}
				case RIGHT -> {
					contentsEnd = this.width - this.width / 10;
					contentsStart = contentsEnd - baseContentsWidth;
				}
				//even though this is exhaustive, javac won't treat contentsStart and contentsEnd as initialized
				//why? who knows! just throw bc this should be impossible
				default -> throw new IllegalStateException(":HOW:");
			}

			if (mouseX <= contentsStart) {
				this.selectionManager.moveCursorToStart();
			} else if (mouseX >= contentsEnd) {
				this.selectionManager.putCursorAtEnd();
			} else {
				int lastWidth = 0;
				for (int i = 1; i < baseContents.length(); i++) {
					String testContents = baseContents.substring(0, i);
					int width = this.textRenderer.getWidth(testContents);
					int midpointWidth = (width + lastWidth) / 2;
					if (mouseX < contentsStart + midpointWidth) {
						this.selectionManager.moveCursorTo(i - 1, false);
						break;
					} else if (mouseX <= contentsStart + width) {
						this.selectionManager.moveCursorTo(i, false);
						break;
					}
					lastWidth = width;
				}
			}
			return true;*/
		//} else {
			return super.mouseClicked(mouseX, mouseY, button);
		//}
	}

	public String getRawLine(int i) {
		var line = this.lines.get(i);
		return extractRaw(line);
	}

	public void setRawLine(int i, String string) {
		var parsed = PARSER.parseText(string, ParserContext.of());

		if (parsed.getString().equals(string)) {
			this.lines.set(i, Text.literal(string));
		} else {
			this.lines.set(i, Text.empty().append(parsed).setStyle(Style.EMPTY.withInsertion(string)));
		}
	}

	public static <T extends StringVisitable> boolean isInBounds(TextRenderer textRenderer, T text) {
		int line_width = textRenderer.getWidth(text);
		String str = (text instanceof Text realText) ? extractRaw(realText) : text.getString();
		return (line_width <= (BG_WIDTH - TXT_X_PADDING)) && (str.length() <= NoteComponent.LINE_LIMIT);
	}

	public static StringVisitable ensureBounds(TextRenderer textRenderer, StringVisitable text) {
		StringVisitable ellipsis = StringVisitable.plain("...");
		return StringVisitable.concat(
			textRenderer.trimToWidth(text, BG_WIDTH - TXT_X_PADDING - textRenderer.getWidth(ellipsis)),
			ellipsis
		);
	}

	public static String extractRaw(Text text) {
		if (text.getStyle() == null) {
			return text.getString();
		}

		var insert = text.getStyle().getInsertion();

		if (insert == null) {
			return text.getString();
		}
		return insert;
	}

	@Override
	public void close() {
		super.close();
		// locked
	}

	@Override
	public ColorPickerWidget colorPickerWidget() {
		return colorPickerWidget;
	}

	@Override
	public void toggleColorPicker(boolean active) {
		colorPickerWidget.toggle(active);
	}

	@Override
	SelectionManager getSelectionManager() {
		return selectionManager;
	}
}
