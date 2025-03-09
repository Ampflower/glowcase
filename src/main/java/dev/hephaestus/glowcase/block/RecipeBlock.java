package dev.hephaestus.glowcase.block;

import java.util.List;

import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class RecipeBlock extends RotatableBlock implements BlockEntityProvider {
	private static final VoxelShape OUTLINE = VoxelShapes.cuboid(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

	@Override
	protected boolean openEditScreen(BlockPos pos) {
		Glowcase.proxy.openRecipeBlockEditScreen(pos);
		return true;
	}

	@Override
	boolean canTarget(PlayerEntity player, BlockPos pos) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RecipeBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof RecipeBlockEntity be)) return ActionResult.CONSUME;

		if (world.isClient) {
			be.openRecipe();
			return ActionResult.SUCCESS;
		}

		return ActionResult.CONSUME;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("block.glowcase.recipe_block.tooltip.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.glowcase.generic.tooltip").formatted(Formatting.DARK_GRAY));
	}

	@Override
	public VoxelShape targetedOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (!(world.getBlockEntity(pos) instanceof RecipeBlockEntity be)) return VoxelShapes.empty();
		float rotation = -(state.get(Properties.ROTATION) * 360) / 16.0F;
		Vector3f offset = new Vector3f(0, 0, be.zOffset == TextBlockEntity.ZOffset.CENTER ? 0F : be.zOffset == TextBlockEntity.ZOffset.FRONT ? 0.4F : -0.4F).rotate(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		return OUTLINE.offset(offset.x, offset.y, offset.z);
	}
}
