package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.DisplayBlockEntity;
import dev.hephaestus.glowcase.packet.C2SEditItemDisplayBlock;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

public class ItemDisplayEditScreen extends DisplayBlockEditScreen {
	private CheckboxWidget renderAsBlockWidget;

	public ItemDisplayEditScreen(DisplayBlockEntity displayBlock) {
		super(displayBlock);
	}

	@Override
    public void init() {
        super.init();

        this.renderAsBlockWidget = CheckboxWidget.builder(Text.translatable("gui.glowcase.render_as_block"), this.client.textRenderer)
			.checked(this.displayBlock.getRenderAsBlock())
			.callback((checkbox, checked) -> this.displayBlock.setRenderAsBlock(checked))
			.pos(20, 197)
			.build();
        
        this.addDrawableChild(this.renderAsBlockWidget);
    }

	@Override
	protected void editDisplayBlock() {
		super.editDisplayBlock();
		C2SEditItemDisplayBlock.of(displayBlock).send();
	}
}
