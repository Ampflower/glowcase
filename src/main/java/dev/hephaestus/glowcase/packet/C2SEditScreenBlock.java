package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record C2SEditScreenBlock(BlockPos pos, float width, float height, ScreenBlockEntity.ZOffset zOffset, boolean eink, boolean stretch, String url, String alt) implements C2SEditBlockEntity {
	public static final Id<C2SEditScreenBlock> ID = new Id<>(Glowcase.id("channel.screen_block"));
	public static final PacketCodec<RegistryByteBuf, C2SEditScreenBlock> PACKET_CODEC = PacketCodec.of(
		(packet, buf) -> {
			String trimmed_url = packet.url.substring(0, Math.min(packet.url.length(), ScreenBlockEntity.URL_MAX_LENGTH));
			String trimmed_alt = packet.alt.substring(0, Math.min(packet.alt.length(), ScreenBlockEntity.ALT_MAX_LENGTH));

			BlockPos.PACKET_CODEC.encode(buf, packet.pos);
			PacketCodecs.FLOAT.encode(buf, packet.width);
			PacketCodecs.FLOAT.encode(buf, packet.height);
			PacketCodecs.BYTE.encode(buf, (byte) packet.zOffset.ordinal());
			PacketCodecs.BOOL.encode(buf, packet.eink);
			PacketCodecs.BOOL.encode(buf, packet.stretch);
			PacketCodecs.STRING.encode(buf, trimmed_url);
			PacketCodecs.STRING.encode(buf, trimmed_alt);
		},
		(buf) -> new C2SEditScreenBlock(BlockPos.PACKET_CODEC.decode(buf),
			PacketCodecs.FLOAT.decode(buf),
			PacketCodecs.FLOAT.decode(buf),
			ScreenBlockEntity.ZOffset.values()[PacketCodecs.BYTE.decode(buf)],
			PacketCodecs.BOOL.decode(buf),
			PacketCodecs.BOOL.decode(buf),
			PacketCodecs.STRING.decode(buf),
			PacketCodecs.STRING.decode(buf))
	);

	public static C2SEditScreenBlock of(ScreenBlockEntity be) {
		return new C2SEditScreenBlock(be.getPos(), be.width, be.height, be.zOffset, be.eink, be.stretch, be.url, be.alt);
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof ScreenBlockEntity be)) return;

		String trimmed_url = url.substring(0, Math.min(url.length(), ScreenBlockEntity.URL_MAX_LENGTH));
		String trimmed_alt = alt.substring(0, Math.min(alt.length(), ScreenBlockEntity.ALT_MAX_LENGTH));

		be.setupScreen(this.width, this.height, this.zOffset, this.eink);
		be.setImage(trimmed_url, trimmed_alt, this.stretch); // Does markDirty and dispatch for us
	}

	@Override
	public Id<C2SEditScreenBlock> getId() {
		return ID;
	}
}
