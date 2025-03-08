package dev.hephaestus.glowcase.block;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ItemDisplayBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDisplayBlock extends AbstractStackInteractableBlock {
	@Override
	protected void openScreen(BlockPos pos) {
		Glowcase.proxy.openItemDisplayBlockEditScreen(pos);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ItemDisplayBlockEntity(pos, state);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("block.glowcase.item_display_block.tooltip.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.glowcase.item_display_block.tooltip.1").formatted(Formatting.DARK_GRAY));
		tooltip.add(Text.translatable("block.glowcase.item_display_block.tooltip.2").formatted(Formatting.DARK_GRAY));
	}
}
