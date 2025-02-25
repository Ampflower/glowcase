package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.GlowcaseBlock;
import dev.hephaestus.glowcase.block.ItemProviderBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemProviderBlockEntity extends AbstractItemDisplayBlockEntity{

	public ItemProviderBlockEntity.GivesItem givesItem = ItemProviderBlockEntity.GivesItem.YES;
	public Set<UUID> givenTo = new HashSet<>();

	private boolean setOffsetAndRotation = false;


	public ItemProviderBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_PROVIDER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);

		tag.putString("gives_item", this.givesItem.name());
		NbtList given = new NbtList();
		for (UUID id : givenTo) {
			NbtCompound givenTag = new NbtCompound();
			givenTag.putUuid("id", id);
			given.add(givenTag);
		}
		tag.put("given_to", given);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		if (tag.contains("gives_item")) {
			this.givesItem = ItemProviderBlockEntity.GivesItem.valueOf(tag.getString("gives_item"));
		} else {
			this.givesItem = ItemProviderBlockEntity.GivesItem.YES;
		}

		givenTo.clear();
		if (tag.contains("given_to")) {
			NbtList given = tag.getList("given_to", NbtElement.COMPOUND_TYPE);
			for (NbtElement elem : given) {
				NbtCompound comp = ((NbtCompound) elem);
				givenTo.add(comp.getUuid("id"));
			}
		}
	}

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack.copy();
		BlockState blockState = world.getBlockState(pos);
		BlockFace blockFace = blockState.get(ItemProviderBlock.FACE);
		if(blockFace != BlockFace.WALL) {
			rotationType = stack.getItem() instanceof BlockItem ? RotationType.TRACKING : RotationType.BILLBOARD;
		}

		this.givenTo.clear();
		this.clearDisplayEntity();
		this.markDirty();
		this.dispatch();
	}

	public void cycleGiveType() {
		switch (this.givesItem) {
			case YES -> this.givesItem = GivesItem.ONCE;
			case ONCE -> this.givesItem = GivesItem.ONE;
			case ONE -> this.givesItem = GivesItem.YES;
		}
		givenTo.clear();
		markDirty();
		dispatch();
	}

	public boolean canGiveTo(PlayerEntity player) {
		if (!hasItem()) return false;
		else return switch (this.givesItem) {
			case YES -> true;
			case ONCE -> player.isCreative() || !givenTo.contains(player.getUuid());
			case ONE -> player.isCreative() || !player.getInventory().containsAny(Set.of(stack.getItem()));
		};
	}

	public void giveTo(PlayerEntity player) {
		ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
		boolean holdingSameAsDisplay = ItemStack.areItemsAndComponentsEqual(getDisplayedStack(), itemStack);

		if (itemStack.isEmpty()) {
			player.setStackInHand(Hand.MAIN_HAND, getDisplayedStack().copy());
		} else if (holdingSameAsDisplay) {
			itemStack.increment(getDisplayedStack().getCount());
			itemStack.capCount(itemStack.getMaxCount());
			player.setStackInHand(Hand.MAIN_HAND, itemStack);
		}
		if (!player.isCreative()) {
			givenTo.add(player.getUuid());
			markDirty();
		}
	}

	public static void tick(World world, BlockPos blockPos, BlockState state, ItemProviderBlockEntity blockEntity) {
		if(!blockEntity.setOffsetAndRotation) {
			BlockFace blockFace = state.get(ItemProviderBlock.FACE);
			if(blockFace == BlockFace.WALL) {
				blockEntity.offset = Offset.BACK;
				blockEntity.rotationType = RotationType.HORIZONTAL;
			}
			else {
				blockEntity.offset = Offset.CENTER;
				blockEntity.rotationType = blockEntity.stack.getItem() instanceof BlockItem ? RotationType.TRACKING : RotationType.BILLBOARD;
			}
			blockEntity.setOffsetAndRotation = true;
			blockEntity.markDirty();
			blockEntity.dispatch();
		}

		if (blockEntity.getDisplayEntity() != null) {
			blockEntity.displayEntity.tick();
			++blockEntity.displayEntity.age;
		}
	}



	public enum GivesItem {
		YES, ONCE, ONE
	}
}
