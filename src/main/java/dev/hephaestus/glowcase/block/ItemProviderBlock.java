package dev.hephaestus.glowcase.block;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.AbstractItemDisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemProviderBlock extends AbstractItemDisplayBlock{

	public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;

	public ItemProviderBlock() {
		super();
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACE);
	}


	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof ItemProviderBlockEntity be)) return ActionResult.CONSUME;

		if (be.canGiveTo(player)) {
			if (!world.isClient) be.giveTo(player);
			return ActionResult.SUCCESS;
		}

		return ActionResult.CONSUME;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof ItemProviderBlockEntity be)) return ItemActionResult.CONSUME;

		if (canEditGlowcase(player, pos)) {
			boolean holdingGlowcaseItem = stack.isIn(Glowcase.ITEM_TAG);
			boolean holdingSameAsDisplay = ItemStack.areItemsEqual(be.getDisplayedStack(), stack);

			if (!be.hasItem()) {
				if (!world.isClient) be.setStack(stack);
				return ItemActionResult.SUCCESS;
			} else if (holdingSameAsDisplay) {
				if (world.isClient) Glowcase.proxy.openItemProviderBlockEditScreen(pos);
				return ItemActionResult.SUCCESS;
			} else if (holdingGlowcaseItem) {
				if (!world.isClient) be.setStack(ItemStack.EMPTY);
				return ItemActionResult.SUCCESS;
			}
		}

		return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ItemProviderBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, Glowcase.ITEM_PROVIDER_BLOCK_ENTITY.get(), AbstractItemDisplayBlockEntity::tick);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("block.glowcase.item_provider_block.tooltip.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.glowcase.item_provider_block.tooltip.1").formatted(Formatting.DARK_GRAY));
		tooltip.add(Text.translatable("block.glowcase.item_provider_block.tooltip.2").formatted(Formatting.DARK_GRAY));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction[] directions = ctx.getPlacementDirections();
		int length = directions.length;

		for(int i = 0; i < length; ++i) {
			Direction direction = directions[i];
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.getDefaultState()
					.with(Properties.ROTATION, MathHelper.floor((double) ((ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15)
					.with(FACE, direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR);
			} else {
				blockState = this.getDefaultState()
					.with(Properties.ROTATION, MathHelper.floor((double) ((ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15)
					.with(FACE, BlockFace.WALL);
			}

			if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				return blockState;
			}
		}

		return null;

	}
}
