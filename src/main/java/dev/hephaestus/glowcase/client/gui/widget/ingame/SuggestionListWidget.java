package dev.hephaestus.glowcase.client.gui.widget.ingame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class SuggestionListWidget<T> extends ClickableWidget {
    private final TextRenderer textRenderer;

    private final List<T> suggestions = new ArrayList<>();
	private @NotNull String filter = "";
    private int scrollOffset = 0;

    private final int baseLineHeight;
    private final int padding;
    private final int maxRows;
	/**
	 * The approximate maximum number of characters that'll fit inside the width of this widget
	 */
	private int characterWidth;

    private final Consumer<T> onSelect;
    private final Function<T, String> toStringFunction;
    
    public SuggestionListWidget(TextRenderer textRenderer, int x, int y, int width, int height, int baseLineHeight, int padding, int maxRows, Consumer<T> onSelect, Function<T, String> toStringFunction) {
        super(x, y, width, height, Text.empty());

        this.baseLineHeight = baseLineHeight;
        this.padding = padding;
        this.maxRows = maxRows;
        this.onSelect = onSelect;
        this.toStringFunction = toStringFunction;
        this.textRenderer = textRenderer;
		this.characterWidth = 1;
		setWidth(width);
    }

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		while (textRenderer.getWidth("m".repeat(characterWidth)) < this.width) {
			characterWidth++;
		}
	}

	// update the suggestion list based on filter
    public void updateSuggestions(List<T> newSuggestions, String filter) {
        suggestions.clear();
		this.filter = filter;

        for (T suggestion : newSuggestions) {
            String text = toStringFunction.apply(suggestion);

            if (text.startsWith(filter)) {
                suggestions.add(suggestion);
            }
        }

        scrollOffset = 0;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (suggestions.isEmpty()) return;
        
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1);
        
        int adjustedLineHeight = baseLineHeight + padding * 2;
        int rows = Math.min(suggestions.size(), maxRows);
        int dynamicHeight = rows * adjustedLineHeight;
        
        context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + dynamicHeight);
        
        int bgWidth = this.width; 
        int bgColor = 0x90000000;
        context.fill(this.getX(), this.getY(), this.getX() + bgWidth, this.getY() + dynamicHeight, bgColor);
        
        int totalLines = suggestions.size();
        int maxLines = rows;
        if (scrollOffset > totalLines - maxLines) {
            scrollOffset = Math.max(0, totalLines - maxLines);
        }
        
        // render each suggestion
        for (int i = 0; i < maxLines; i++) {
            int suggestionIndex = i + scrollOffset;
            if (suggestionIndex >= totalLines) break;
            
            T suggestion = suggestions.get(suggestionIndex);
            String suggestionText = toStringFunction.apply(suggestion);
            int suggestionY = getY() + i * adjustedLineHeight;
            
            // highlight hovered suggestion
            if (mouseX >= this.getX() && mouseX <= this.getX() + bgWidth && mouseY >= suggestionY && mouseY < suggestionY + adjustedLineHeight) {
                context.fill(this.getX(), suggestionY, this.getX() + bgWidth, suggestionY + adjustedLineHeight, 0xFFAAAAAA);
            }

			// Detect if the text is too long, and cut it off so more relevant things are visible
			if (filter.length() > 5) {
				if (textRenderer.getWidth(suggestionText) > this.width) {
					suggestionText = "…"+suggestionText.substring(Math.min(filter.length(), suggestionText.length()-characterWidth/2));
				}
			}

            context.drawTextWithShadow(textRenderer, Text.literal(suggestionText), this.getX() + padding, suggestionY + padding, 0xFFFFFF);
        }

        // scrollbar thingy
        if (totalLines > rows) {
            int scrollbarWidth = 4;

            int sbX = getX() + getWidth() - scrollbarWidth;
            int sbY = getY();

            int scrollbarHeight = dynamicHeight;
            int scrollBarBgColor = 0xC0000000;

            context.fill(sbX, sbY, sbX + scrollbarWidth, sbY + scrollbarHeight, scrollBarBgColor);
            
            float visibleRatio = (float) rows / totalLines;
            int handleHeight = Math.max((int)(visibleRatio * dynamicHeight), 4);

            int availableScroll = totalLines - rows;
            int handleYOffset = availableScroll > 0 ? (int)(((float)scrollOffset / availableScroll) * (dynamicHeight - handleHeight)) : 0;
            int handleY = getY() + handleYOffset;
            
            context.fill(sbX, handleY, sbX + scrollbarWidth, handleY + handleHeight, 0xFFFFFFFF);
        }
        
        context.disableScissor();
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int adjustedLineHeight = baseLineHeight + padding * 2;
        int relativeY = (int)mouseY - this.getY();
        int clickedIndex = relativeY / adjustedLineHeight + scrollOffset;

        if (clickedIndex >= 0 && clickedIndex < suggestions.size()) {
            onSelect.accept(suggestions.get(clickedIndex));
            return true;
        }

        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int rows = Math.min(suggestions.size(), maxRows);
        
        int totalLines = suggestions.size();
        int maxLines = rows;
        scrollOffset -= (int) verticalAmount;
        
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > totalLines - maxLines) scrollOffset = Math.max(0, totalLines - maxLines);
        
        return true;
    }
    
    @Override
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {}
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int adjustedLineHeight = baseLineHeight + padding * 2;
        int rows = Math.min(suggestions.size(), maxRows);
        int dynamicHeight = rows * adjustedLineHeight;
        
        return mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + dynamicHeight;
    }
}
