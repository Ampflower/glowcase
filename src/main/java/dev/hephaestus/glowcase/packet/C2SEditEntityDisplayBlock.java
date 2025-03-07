package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.AbstractDisplayBlockEntity;
import dev.hephaestus.glowcase.util.DisplayBlockSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditEntityDisplayBlock(BlockPos pos, DisplayBlockSettings settings) implements C2SEditBlockEntity, C2SEditDisplayBlock {
	public static final Id<C2SEditEntityDisplayBlock> ID = new Id<>(Glowcase.id("channel.entity_display"));
	public static final PacketCodec<RegistryByteBuf, C2SEditEntityDisplayBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditEntityDisplayBlock::pos,
		DisplayBlockSettings.PACKET_CODEC, C2SEditEntityDisplayBlock::settings,
		C2SEditEntityDisplayBlock::new
	);

	public static C2SEditEntityDisplayBlock of(AbstractDisplayBlockEntity be) {
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
