package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.DisplayBlockEntity;
import dev.hephaestus.glowcase.util.DisplayBlockSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditItemDisplayBlock(BlockPos pos, DisplayBlockSettings settings) implements C2SEditBlockEntity, C2SEditDisplayBlock {
	public static final Id<C2SEditItemDisplayBlock> ID = new Id<>(Glowcase.id("channel.item_display"));
	public static final PacketCodec<RegistryByteBuf, C2SEditItemDisplayBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditItemDisplayBlock::pos,
		DisplayBlockSettings.PACKET_CODEC, C2SEditItemDisplayBlock::settings,
		C2SEditItemDisplayBlock::new
	);

	public static C2SEditEntityDisplayBlock of(DisplayBlockEntity be) {
		return new C2SEditEntityDisplayBlock(be.getPos(), be.toSettings());
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}


	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		C2SEditDisplayBlock.super.receive(world, blockEntity);
	}
}
