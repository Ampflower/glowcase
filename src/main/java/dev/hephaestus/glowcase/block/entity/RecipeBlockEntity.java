package dev.hephaestus.glowcase.block.entity;

import org.jetbrains.annotations.Nullable;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.client.render.block.entity.BakedBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RecipeBlockEntity extends BlockEntity {
	
	public String recipe = "diamond_sword";
	public TextBlockEntity.ZOffset zOffset = TextBlockEntity.ZOffset.CENTER;
	public int color = 0xFFFFFF;

	public RecipeBlockEntity(BlockPos pos, BlockState state) {
		super(Glowcase.RECIPE_BLOCK_ENTITY.get(), pos, state);
	}

	@Environment(EnvType.CLIENT)
	public void openRecipe() {
		Identifier rid = Identifier.tryParse(recipe);
		if (rid == null) {
			return;
		}
		EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(rid);
		if (recipe == null) {
			return;
		}
		EmiApi.displayRecipe(recipe);
	}

	public void setRecipe(String newRecipe) {
		recipe = newRecipe;
		markDirty();
		dispatch();
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);

		tag.putString("recipe", this.recipe);
		tag.putString("z_offset", this.zOffset.name());
		tag.putInt("color", this.color);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);

		this.recipe = tag.getString("recipe");
		this.zOffset = TextBlockEntity.ZOffset.valueOf(tag.getString("z_offset"));
		this.color = tag.getInt("color");
	}

	@SuppressWarnings({"MethodCallSideOnly", "VariableUseSideOnly"})
	@Override
	public void markRemoved() {
		if (world != null && world.isClient) {
			BakedBlockEntityRenderer.Manager.markForRebuild(getPos());
		}
		super.markRemoved();
	}

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
