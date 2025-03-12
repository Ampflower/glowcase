package dev.hephaestus.glowcase.block.entity;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class SpriteBlockEntity extends GlowcaseBlockEntity {
	public String sprite = "arrow";
	public int rotation = 0;
	public TextBlockEntity.ZOffset zOffset = TextBlockEntity.ZOffset.BACK;
	public int color = 0xFFFFFF;

	public SpriteBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.SPRITE_BLOCK_ENTITY.get(), pos, state);
	}

	public void setSprite(String newSprite) {
		sprite = newSprite;
		markDirty();
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);

		tag.putString("sprite", this.sprite);
		tag.putInt("rotation", this.rotation);
		tag.putString("z_offset", this.zOffset.name());
		tag.putInt("color", this.color);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		this.sprite = tag.getString("sprite");
		this.rotation = tag.getInt("rotation");
		this.zOffset = TextBlockEntity.ZOffset.valueOf(tag.getString("z_offset"));
		this.color = tag.getInt("color");
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
		markDirty();
	}
}
