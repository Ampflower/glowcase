package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.RecipeBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditRecipeBlock(BlockPos pos, String recipe, TextBlockEntity.ZOffset offset, float rotationX, float rotationY) implements C2SEditBlockEntity {
	public static final Id<C2SEditRecipeBlock> ID = new Id<>(Glowcase.id("channel.recipe.save"));
	public static final PacketCodec<RegistryByteBuf, C2SEditRecipeBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditRecipeBlock::pos,
		PacketCodecs.STRING, C2SEditRecipeBlock::recipe,
		PacketCodecs.INTEGER.xmap(index -> TextBlockEntity.ZOffset.values()[index], TextBlockEntity.ZOffset::ordinal), C2SEditRecipeBlock::offset,
		PacketCodecs.FLOAT, C2SEditRecipeBlock::rotationX,
		PacketCodecs.FLOAT, C2SEditRecipeBlock::rotationY,
		C2SEditRecipeBlock::new
	);

	public static C2SEditRecipeBlock of(RecipeBlockEntity be) {
		return new C2SEditRecipeBlock(be.getPos(), be.recipe, be.zOffset, be.rotationX, be.rotationY);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof RecipeBlockEntity be)) return;

		be.setRecipe(this.recipe());
		be.zOffset = this.offset();
		be.rotationX = this.rotationX();
		be.rotationY = this.rotationY();

		be.markDirty();
	}
}
