package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ScreenBlockEntity extends BlockEntity {
	public String url = "";
	public String alt = "";

	public float width = 1f;
	public float height = 1f;
	public TextBlockEntity.ZOffset zOffset = TextBlockEntity.ZOffset.CENTER;

	public boolean stretch = false;
	public boolean eink = true;

	public ScreenBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.SCREEN_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);

		nbt.putFloat("width", width);
		nbt.putFloat("height", height);
		nbt.putBoolean("stretch", stretch);
		nbt.putBoolean("eink", eink);
		nbt.putString("z_offset", this.zOffset.name());

		nbt.putString("url", url);
		nbt.putString("alt", alt);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);

		width = nbt.getFloat("width");
		height = nbt.getFloat("height");
		stretch = nbt.getBoolean("stretch");
		eink = nbt.getBoolean("eink");
		this.zOffset = TextBlockEntity.ZOffset.valueOf(nbt.getString("z_offset"));

		url = nbt.getString("url");
		alt = nbt.getString("alt");

		markDirty();
		dispatch();
	}

	public void setImage(String url, String alt, boolean stretch) {
		this.url = url;
		this.alt = alt;
		this.stretch = stretch;
		markDirty();
		dispatch();
	}

	public void setupScreen(float width, float height, TextBlockEntity.ZOffset zOffset, boolean eink) {
		this.width = Math.min(0.05f, width);
		this.height = Math.min(0.05f, height);
		this.zOffset = zOffset;
		this.eink = eink;
		markDirty();
		dispatch();
	}

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
