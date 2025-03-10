package dev.hephaestus.glowcase.block.entity;

import com.mojang.logging.LogUtils;
import dev.hephaestus.glowcase.Glowcase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class SoundPlayerBlockEntity extends GlowcaseBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();

	public Identifier soundId = SoundEvents.ENTITY_CAT_PURREOW.getId();
	public SoundCategory category = SoundCategory.BLOCKS;
	public float volume = 1;
	public float pitch = 1;
	public int repeatDelay = 0;
	public float distance = 16;
	public boolean relative = false;
	public Vec3d offset = Vec3d.ZERO;
	public boolean cancelOthers = false;

	public PositionedSoundLoop nowPlaying = null;

	public SoundPlayerBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.SOUND_BLOCK_ENTITY.get(), pos, state);
	}

	public void cycleCategory() {
		this.category = SoundCategory.values()[(this.category.ordinal() + 1) % SoundCategory.values().length];
	}

	@Override
	protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);

		RegistryOps<NbtElement> ops = registryLookup.getOps(NbtOps.INSTANCE);
		Identifier.CODEC.encodeStart(ops, this.soundId)
			.resultOrPartial(LOGGER::error)
			.ifPresent(result -> tag.put("sound", result));
		tag.putString("category", this.category.toString());
		tag.putFloat("volume", this.volume);
		tag.putFloat("pitch", this.pitch);
		tag.putInt("repeatDelay", this.repeatDelay);
		tag.putFloat("distance", this.distance);
		tag.putBoolean("relative", this.relative);
		tag.putBoolean("cancelOthers", this.cancelOthers);
		Vec3d.CODEC.encodeStart(ops, this.offset)
			.resultOrPartial(LOGGER::error)
			.ifPresent(result -> tag.put("offset", result));
	}

	@Override
	protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		RegistryOps<NbtElement> ops = registryLookup.getOps(NbtOps.INSTANCE);
		if (tag.contains("sound"))
			Identifier.CODEC.parse(ops, tag.get("sound"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(result -> this.soundId = result);
		this.category = SoundCategory.valueOf(tag.getString("category"));
		this.volume = tag.getFloat("volume");
		this.pitch = tag.getFloat("pitch");
		this.repeatDelay = tag.getInt("repeatDelay");
		this.distance = tag.getFloat("distance");
		this.relative = tag.getBoolean("relative");
		this.cancelOthers = tag.getBoolean("cancelOthers");
		if (tag.contains("offset"))
			Vec3d.CODEC.parse(ops, tag.get("offset"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(result -> this.offset = result);
	}

	@Environment(EnvType.CLIENT)
	public static void clientTick(World world, BlockPos pos, BlockState state, SoundPlayerBlockEntity entity) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player instanceof ClientPlayerEntity player) {
			PositionedSoundLoop sound = new PositionedSoundLoop(
				entity.soundId, entity.category,
				entity.volume, entity.pitch, entity.repeatDelay,
				entity.distance,
				entity.relative,
				entity.getSoundPos(),
				entity.getSourcePos(),
				player,
				entity.getPos()
			);

//			LOGGER.info("done? " + (entity.nowPlaying == null ? "null" : entity.nowPlaying.isDone()));
			if (entity.nowPlaying == null || entity.nowPlaying.isDone() || entity.nowPlaying.isDifferentFrom(sound)) {
				mc.getSoundManager().stop(entity.nowPlaying);
//				LOGGER.info("playing");
				if (sound.inRange()) {
					if (entity.cancelOthers) mc.getSoundManager().stopSounds(null, entity.category);
					mc.getSoundManager().play(sound);
					entity.nowPlaying = sound;
				}
			}
		}
	}

	private Vec3d getSoundPos() {
		if(relative) {
			return offset;
		}

		return pos.toCenterPos().add(offset);
	}

	private Vec3d getSourcePos() {
		if(relative) {
			return pos.toCenterPos();
		}

		return pos.toCenterPos().add(offset);
	}

	// I don't think the repeat is necessary on this at this point
	public static class PositionedSoundLoop extends PositionedSoundInstance implements TickableSoundInstance {
		private final PlayerEntity player;
		private final BlockPos soundBlockPos;
		private final Vec3d pos;

		private final float distance;

		private boolean done;

		public PositionedSoundLoop(Identifier id, SoundCategory category, float volume, float pitch, int repeatDelay, float distance, boolean relative, Vec3d soundPos, Vec3d pos, PlayerEntity player, BlockPos soundBlockPos) {
			super(
				id, category,
				volume, pitch,
				SoundInstance.createRandom(),
				true, repeatDelay,
				AttenuationType.NONE,
				soundPos.x, soundPos.y, soundPos.z,
				relative
			);

			this.pos = pos;
			this.player = player;
			this.soundBlockPos = soundBlockPos;
			this.distance = distance;
			this.done = false;
		}

		@Override
		public boolean isDone() {
			return this.done;
		}

		public void setDone() {
			this.done = true;
		}

		@Override
		public void tick() {
			// stops track-stacking when reloading the block
			if (!inRange() || !(this.player.getWorld().getBlockEntity(this.soundBlockPos) instanceof SoundPlayerBlockEntity be && !this.isDifferentFrom(be.nowPlaying))) {
				setDone();
			}
		}

		@Override
		public float getVolume() {
			var originalVolume = super.getVolume();

			return originalVolume * linearFalloff();
		}

		private float linearFalloff() {
			float distanceToPlayer = (float) this.player.getPos().distanceTo(this.pos);
			return 1 - (distanceToPlayer / distance);
		}

		public boolean inRange() {
			return this.player.squaredDistanceTo(this.pos) <= this.distance * this.distance;
		}

		public boolean isDifferentFrom(PositionedSoundLoop other) {
			return !(
				other != null &&
					this.id.equals(other.id) &&
					this.category.equals(other.category) &&
					this.volume == other.volume &&
					this.pitch == other.pitch &&
					this.repeatDelay == other.repeatDelay &&
					this.distance == other.distance &&
					this.relative == other.relative &&
					this.x == other.x &&
					this.y == other.y &&
					this.z == other.z
			);
		}
	}
}
