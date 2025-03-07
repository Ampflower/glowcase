package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemDisplayBlockEntity extends AbstractItemDisplayBlockEntity {

	public float scale = 1.0F;

	public float xOffset = 0.0F;
	public float yOffset = 0.0F;
	public float zOffset = 0.0F;

	public ItemDisplayBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.ITEM_DISPLAY_BLOCK_ENTITY.get(), pos, state);
		showName = false;
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);
		tag.putFloat("scale", scale);

		tag.putFloat("xOffset", xOffset);
		tag.putFloat("yOffset", yOffset);
		tag.putFloat("zOffset", zOffset);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		if(tag.contains("scale")) {
			this.scale = tag.getFloat("scale");
		}

		if(tag.contains("xOffset")){
			this.xOffset = tag.getFloat("xOffset");
		}

		if(tag.contains("yOffset")){
			this.yOffset = tag.getFloat("yOffset");
		}

		if(tag.contains("zOffset")){
			this.zOffset = tag.getFloat("zOffset");
		}
	}

	public boolean hasItem() {
		return this.stack != null && !this.stack.isEmpty();
	}

	public void setStack(ItemStack stack) {
		this.stack = stack.copy();

		this.markDirty();
		this.dispatch();
	}

	public ItemStack getDisplayedStack() {
		return this.stack;
	}

	//TODO: these cycleXxx methods are only used on ItemDisplayBlockEditScreen, and can probably be moved there
	// -> yes, that means the setBlockState call is wacky
	public void cycleRotationType(PlayerEntity playerEntity) {
		switch (this.rotationType) {
			case TRACKING -> this.rotationType = RotationType.BILLBOARD;
			case BILLBOARD -> {
				this.rotationType = RotationType.HORIZONTAL;
				if (this.world != null) {
					this.world.setBlockState(this.pos, this.getCachedState().with(Properties.ROTATION, MathHelper.floor((double) ((playerEntity.getYaw()) * 16.0F / 360.0F) + 0.5D) & 15));
				}
			}
			case HORIZONTAL -> this.rotationType = RotationType.LOCKED;
			case LOCKED -> this.rotationType = RotationType.TRACKING;
		}
		markDirty();
		dispatch();
	}

	public void cycleOffset() {
		switch (this.offset) {
			case CENTER -> this.offset = Offset.BACK;
			case BACK -> this.offset = Offset.FRONT;
			case FRONT -> this.offset = Offset.CENTER;
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

	public static void tick(World world, BlockPos blockPos, BlockState state, ItemDisplayBlockEntity blockEntity) {
		//does nothing right now
	}
}
