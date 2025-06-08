package dev.hephaestus.glowcase.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ampflower
 **/
public abstract class WaterloggableGlowcaseBlock extends GlowcaseBlock implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public WaterloggableGlowcaseBlock() {
		this(defaultSettings());
	}

	public WaterloggableGlowcaseBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
	}

	@Override
	protected boolean isTransparent(final BlockState state, final BlockView world, final BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		final BlockState state,
		final Direction direction,
		final BlockState neighborState,
		final WorldAccess world,
		final BlockPos pos,
		final BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(final BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public @Nullable BlockState getPlacementState(final ItemPlacementContext ctx) {
		return getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	protected void appendProperties(final StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public ItemStack tryDrainFluid(
		@Nullable final PlayerEntity player,
		final WorldAccess world,
		final BlockPos pos,
		final BlockState state
	) {
		if (!GlowcaseBlock.canEditGlowcase(player, pos)) {
			return ItemStack.EMPTY;
		}
		return Waterloggable.super.tryDrainFluid(player, world, pos, state);
	}

	@Override
	public boolean canFillWithFluid(
		@Nullable final PlayerEntity player,
		final BlockView world,
		final BlockPos pos,
		final BlockState state,
		final Fluid fluid
	) {
		return GlowcaseBlock.canEditGlowcase(player, pos) && Waterloggable.super.canFillWithFluid(player, world, pos, state, fluid);
	}
}
