package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemProviderBlockEntity extends GlowcaseBlockEntity implements InfiniteInventory, StackInteractable {
	protected ItemStack stack = ItemStack.EMPTY;
	protected GivesItem givesItem = GivesItem.ALWAYS;
	protected final Set<UUID> givenTo = new HashSet<>();

	public ItemProviderBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_PROVIDER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public boolean matchesStack(ItemStack stack) {
		return ItemStack.areItemsEqual(this.stack, stack);
	}

	@Override
	public void setFromStack(ItemStack stack) {
		this.stack = stack.copy();
		this.givenTo.clear();
		this.markDirty();
	}

	@Override
	public void unsetFromStack() {
		this.stack = ItemStack.EMPTY;
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	public GivesItem getGivesItem() {
		return givesItem;
	}

	public void setGivesItem(GivesItem givesItem) {
		this.givesItem = givesItem;
		markDirty();
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);
		if (!this.stack.isEmpty()) tag.put("item", this.stack.encode(registryLookup));
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
		this.stack = tag.contains("item", NbtElement.COMPOUND_TYPE) ? ItemStack.fromNbt(registryLookup, tag.getCompound("item")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
		if (tag.contains("gives_item")) {
			this.givesItem = GivesItem.valueOf(tag.getString("gives_item"));
		} else {
			this.givesItem = GivesItem.ALWAYS;
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

	public void cycleGiveType() {
		this.givesItem = GivesItem.values()[(this.givesItem.ordinal() + 1) % GivesItem.values().length];
		givenTo.clear();
		markDirty();
	}

	public boolean canGiveTo(PlayerEntity player) {
		if (!hasItem()) return false;
		else return switch (this.givesItem) {
			case ALWAYS -> true;
			case ONCE -> player.isCreative() || !givenTo.contains(player.getUuid());
			case ONE -> player.isCreative() || !player.getInventory().containsAny(Set.of(stack.getItem()));
		};
	}

	public void giveTo(PlayerEntity player) {
		ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
		boolean holdingSameAsDisplay = ItemStack.areItemsAndComponentsEqual(getStack(), itemStack);

		if (itemStack.isEmpty()) {
			player.setStackInHand(Hand.MAIN_HAND, getStack().copy());
		} else if (holdingSameAsDisplay) {
			itemStack.increment(getStack().getCount());
			itemStack.capCount(itemStack.getMaxCount());
			player.setStackInHand(Hand.MAIN_HAND, itemStack);
		}
		if (!player.isCreative()) {
			givenTo.add(player.getUuid());
			markDirty();
		}
	}

	public static Vec2f getPitchAndYaw(Entity camera, BlockPos pos, float delta) {
		double d = pos.getX() - camera.getLerpedPos(delta).x + 0.5;
		double e = pos.getY() - camera.getEyeY() + 0.5;
		double f = pos.getZ() - camera.getLerpedPos(delta).z + 0.5;
		double g = MathHelper.sqrt((float) (d * d + f * f));

		float pitch = (float) ((-MathHelper.atan2(e, g)));
		float yaw = (float) (-MathHelper.atan2(f, d) + Math.PI / 2);

		return new Vec2f(pitch, yaw);
	}

	public enum GivesItem {
		ALWAYS, ONCE, ONE
	}
}
