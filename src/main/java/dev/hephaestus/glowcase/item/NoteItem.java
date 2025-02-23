package dev.hephaestus.glowcase.item;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NoteItem extends Item {
	public NoteItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stackInHand = user.getStackInHand(hand);

		if (world.isClient())
			Glowcase.proxy.openNoteEditScreen(stackInHand);

		return TypedActionResult.success(stackInHand);
	}
}
