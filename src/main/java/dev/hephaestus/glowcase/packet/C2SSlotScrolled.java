package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record C2SSlotScrolled(int syncId, int revision, int slotIndex, int amount) implements CustomPayload {
	public static final Id<C2SSlotScrolled> ID = new Id<>(Glowcase.id("slot_scrolled"));
	public static final PacketCodec<RegistryByteBuf, C2SSlotScrolled> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT, C2SSlotScrolled::syncId,
		PacketCodecs.VAR_INT, C2SSlotScrolled::revision,
		PacketCodecs.VAR_INT, C2SSlotScrolled::slotIndex,
		PacketCodecs.VAR_INT, C2SSlotScrolled::amount,
		C2SSlotScrolled::new
	);

	@Override
	public Id<C2SSlotScrolled> getId() {
		return ID;
	}
}
