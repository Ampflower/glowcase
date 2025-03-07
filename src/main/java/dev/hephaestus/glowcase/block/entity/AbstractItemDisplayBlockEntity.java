package dev.hephaestus.glowcase.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractItemDisplayBlockEntity extends BlockEntity implements Inventory {

	protected ItemStack stack = ItemStack.EMPTY;
	protected Entity displayEntity = null;

	public AbstractItemDisplayBlockEntity.RotationType rotationType = AbstractItemDisplayBlockEntity.RotationType.TRACKING;

	public AbstractItemDisplayBlockEntity.Offset offset = AbstractItemDisplayBlockEntity.Offset.CENTER;
	public boolean showName = true;
	public float pitch;
	public float yaw;

	public AbstractItemDisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ItemStack getStack(){
		return stack;
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);
		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.encode(registryLookup));
		}
		tag.putString("rotation_type", this.rotationType.name());
		tag.putFloat("pitch", this.pitch);
		tag.putFloat("yaw", this.yaw);
		tag.putBoolean("show_name", this.showName);
		tag.putString("offset", this.offset.name());
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		this.stack = tag.contains("item", NbtElement.COMPOUND_TYPE)
			? ItemStack.fromNbt(registryLookup, tag.getCompound("item")).orElse(ItemStack.EMPTY)
			: ItemStack.EMPTY;
		this.clearDisplayEntity();

		if (tag.contains("tracking")) {
			this.rotationType = tag.getBoolean("tracking") ? AbstractItemDisplayBlockEntity.RotationType.TRACKING : AbstractItemDisplayBlockEntity.RotationType.LOCKED;
		} else if (tag.contains("rotation_type")) {
			this.rotationType = AbstractItemDisplayBlockEntity.RotationType.valueOf(tag.getString("rotation_type"));
		} else {
			this.rotationType = AbstractItemDisplayBlockEntity.RotationType.TRACKING;
		}


		if (tag.contains("offset")) {
			this.offset = AbstractItemDisplayBlockEntity.Offset.valueOf(tag.getString("offset"));
		} else {
			this.offset = AbstractItemDisplayBlockEntity.Offset.CENTER;
		}

		if (tag.contains("pitch")) {
			this.pitch = tag.getFloat("pitch");
			this.yaw = tag.getFloat("yaw");
		}

		if (tag.contains("show_name")) {
			this.showName = tag.getBoolean("show_name");
		}

	}

	public boolean hasItem() {
		return this.stack != null && !this.stack.isEmpty();
	}

	public void setStack(ItemStack stack) {
		this.stack = stack.copy();

		this.clearDisplayEntity();
		this.markDirty();
		this.dispatch();
	}

	protected void clearDisplayEntity() {
		this.displayEntity = null;
	}

	public Entity getDisplayEntity() {
		if (this.displayEntity == null && this.world != null && this.stack.getItem() instanceof SpawnEggItem eggItem) {
			this.displayEntity = eggItem.getEntityType(this.stack).create(this.world);
		}

		return this.displayEntity;
	}

	public ItemStack getDisplayedStack() {
		return this.stack;
	}

	public void cycleRotationType(PlayerEntity playerEntity) {
		switch (this.rotationType) {
			case TRACKING -> this.rotationType = AbstractItemDisplayBlockEntity.RotationType.BILLBOARD;
			case BILLBOARD -> {
				this.rotationType = AbstractItemDisplayBlockEntity.RotationType.HORIZONTAL;
				if (this.world != null) {
					this.world.setBlockState(this.pos, this.getCachedState().with(Properties.ROTATION, MathHelper.floor((double) ((playerEntity.getYaw()) * 16.0F / 360.0F) + 0.5D) & 15));
				}
			}
			case HORIZONTAL -> this.rotationType = AbstractItemDisplayBlockEntity.RotationType.LOCKED;
			case LOCKED -> this.rotationType = AbstractItemDisplayBlockEntity.RotationType.TRACKING;
		}
		markDirty();
		dispatch();
	}

	public void cycleOffset() {
		switch (this.offset) {
			case CENTER -> this.offset = AbstractItemDisplayBlockEntity.Offset.BACK;
			case BACK -> this.offset = AbstractItemDisplayBlockEntity.Offset.FRONT;
			case FRONT -> this.offset = AbstractItemDisplayBlockEntity.Offset.CENTER;
		}
		markDirty();
		dispatch();
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

	public static void tick(World world, BlockPos blockPos, BlockState state, AbstractItemDisplayBlockEntity blockEntity) {
		if (blockEntity.getDisplayEntity() != null) {
			blockEntity.displayEntity.tick();
			++blockEntity.displayEntity.age;
		}
	}

	public enum RotationType {
		LOCKED, TRACKING, HORIZONTAL, BILLBOARD
	}


	public enum Offset {
		CENTER, BACK, FRONT
	}

	// hopper extraction

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return stack.copyWithCount(1);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return stack.copyWithCount(1);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return stack.copyWithCount(1);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {}

	// standard blockentity boilerplate

	public void dispatch() {
		if (world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
}
