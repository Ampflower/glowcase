package dev.hephaestus.glowcase.block;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.StackInteractable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractStackInteractableBlock extends GlowcaseBlock implements BlockEntityProvider {
	public AbstractStackInteractableBlock() {
		super();
	}

	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		return ActionResult.CONSUME;
	}

	protected abstract void openScreen(BlockPos pos);

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof StackInteractable be)) return ItemActionResult.CONSUME;

		if (canEditGlowcase(player, pos)) {
			boolean holdingGlowcaseItem = stack.isIn(Glowcase.ITEM_TAG);
			boolean holdingSameAsDisplay = be.matchesStack(stack);

			if (!be.matchesStack(ItemStack.EMPTY)) {
				if (!world.isClient) be.setFromStack(stack);
				return ItemActionResult.SUCCESS;
			} else if (holdingSameAsDisplay) {
				if (world.isClient) openScreen(pos);
				return ItemActionResult.SUCCESS;
			} else if (holdingGlowcaseItem) {
				if (!world.isClient) be.unsetFromStack();
				return ItemActionResult.SUCCESS;
			}
		}

		return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}
