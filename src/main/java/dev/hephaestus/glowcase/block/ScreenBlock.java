package dev.hephaestus.glowcase.block;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScreenBlock extends GlowcaseBlock implements BlockEntityProvider {
	public ScreenBlock() {
		super();
		this.setDefaultState(this.getDefaultState().with(Properties.ROTATION, 0));
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ScreenBlockEntity(pos, state);
	}

	@Override
	protected boolean openEditScreen(BlockPos pos) {
		Glowcase.proxy.openScreenBlockEditScreen(pos);
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.ROTATION);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(Properties.ROTATION, MathHelper.floor((double) ((180.0F + ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15);
	}

	@Override
	boolean canTarget(PlayerEntity player, BlockPos pos) {
		return super.canTarget(player, pos) || player.getMainHandStack().isOf(Glowcase.TABLET_ITEM.get());
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("block.glowcase.screen_block.tooltip.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.glowcase.generic.tooltip").formatted(Formatting.DARK_GRAY));
	}
}
