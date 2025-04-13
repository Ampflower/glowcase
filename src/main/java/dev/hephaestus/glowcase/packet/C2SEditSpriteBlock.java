package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.SpriteBlockEntity;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditSpriteBlock(BlockPos pos, String sprite, TextBlockEntity.ZOffset offset, int color, float scale, double offsetX, double offsetY, double offsetZ, float pitch, float yaw) implements C2SEditBlockEntity {
	public static final Id<C2SEditSpriteBlock> ID = new Id<>(Glowcase.id("channel.sprite.save"));
	public static final PacketCodec<RegistryByteBuf, C2SEditSpriteBlock> PACKET_CODEC = PacketCodec.of(
		(packet, buf) -> {
			BlockPos.PACKET_CODEC.encode(buf, packet.pos());
			PacketCodecs.STRING.encode(buf, packet.sprite());
			PacketCodecs.BYTE.encode(buf, (byte) packet.offset().ordinal());
			PacketCodecs.INTEGER.encode(buf, packet.color());
			PacketCodecs.FLOAT.encode(buf, packet.scale());
			PacketCodecs.DOUBLE.encode(buf, packet.offsetX());
			PacketCodecs.DOUBLE.encode(buf, packet.offsetY());
			PacketCodecs.DOUBLE.encode(buf, packet.offsetZ());
			PacketCodecs.FLOAT.encode(buf, packet.pitch());
			PacketCodecs.FLOAT.encode(buf, packet.yaw());
		},
		(buf) -> new C2SEditSpriteBlock(
			BlockPos.PACKET_CODEC.decode(buf),
			PacketCodecs.STRING.decode(buf),
			TextBlockEntity.ZOffset.values()[PacketCodecs.BYTE.decode(buf)],
			PacketCodecs.INTEGER.decode(buf),
			PacketCodecs.FLOAT.decode(buf),
			PacketCodecs.DOUBLE.decode(buf),
			PacketCodecs.DOUBLE.decode(buf),
			PacketCodecs.DOUBLE.decode(buf),
			PacketCodecs.FLOAT.decode(buf),
			PacketCodecs.FLOAT.decode(buf)
		)
	);

	public static C2SEditSpriteBlock of(SpriteBlockEntity be) {
		return new C2SEditSpriteBlock(be.getPos(), be.getSprite(), be.zOffset, be.color, be.scale, be.offsetX, be.offsetY, be.offsetZ, be.pitch, be.yaw);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof SpriteBlockEntity be)) return;

		be.setSprite(this.sprite());
		be.zOffset = this.offset();
		be.color = this.color();
		be.scale = this.scale();
		be.offsetX = this.offsetX();
		be.offsetY = this.offsetY();
		be.offsetZ = this.offsetZ();
		be.pitch = this.pitch();
		be.yaw = this.yaw();

		be.markDirty();
	}
}
