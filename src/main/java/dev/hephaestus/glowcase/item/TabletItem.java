package dev.hephaestus.glowcase.item;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static dev.hephaestus.glowcase.block.GlowcaseBlock.canEditGlowcase;

public class TabletItem extends Item {
	public TabletItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		if (world.isClient() && !user.isSneaking()) {
			// Open Editor on Client
			Glowcase.proxy.openTabletEditScreen(stack);
			return TypedActionResult.success(user.getStackInHand(hand));
		}

		return TypedActionResult.success(user.getStackInHand(hand));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		BlockPos pos = context.getBlockPos();
		ItemStack stack = context.getStack();
		World world = context.getWorld();

		if (world.isClient())
			return ActionResult.PASS;

		// Update linked block
		if (player != null && player.isSneaking() && world.getBlockEntity(pos) instanceof ScreenBlockEntity) {
			if (canEditGlowcase(player, pos)) {
				stack.set(Glowcase.LINKED_SCREEN_COMPONENT.get(), pos);
				player.sendMessage(Text.translatable("gui.glowcase.updated_linked_screen", pos.toShortString()), true);

				return ActionResult.SUCCESS;
			} else
				player.sendMessage(Text.translatable("gui.glowcase.linking_denied"), true);
		}

		return ActionResult.PASS;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("item.glowcase.tablet.tooltip.0").formatted(Formatting.GRAY));
	}
}
