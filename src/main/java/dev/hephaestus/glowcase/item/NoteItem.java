package dev.hephaestus.glowcase.item;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.item.component.NoteComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

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

	@Override
	public Text getName(ItemStack stack) {
		if (stack.contains(Glowcase.NOTE_COMPONENT.get())) {
			NoteComponent noteComponent = stack.get(Glowcase.NOTE_COMPONENT.get());
			assert noteComponent != null;
			if (noteComponent.title().isPresent())
				return Text.literal(noteComponent.title().get()).setStyle(Style.EMPTY.withItalic(true));
		}
		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		if (itemStack.contains(Glowcase.NOTE_COMPONENT.get())) {
			NoteComponent noteComponent = itemStack.get(Glowcase.NOTE_COMPONENT.get());
			assert noteComponent != null;

			if (noteComponent.title().isPresent()) {
				Text author = (noteComponent.author().isPresent())
					? Text.literal(noteComponent.author().get())
					: Text.translatable("gui.glowcase.note.anonymous");

				tooltip.add(Text.translatable("item.glowcase.note.tooltip.0", author).formatted(Formatting.YELLOW));
			}
		}

		tooltip.add(Text.translatable("item.glowcase.note.tooltip.1").formatted(Formatting.GRAY));
	}
}
