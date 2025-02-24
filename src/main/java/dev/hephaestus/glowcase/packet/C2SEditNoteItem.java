package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.item.component.NoteComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record C2SEditNoteItem(NoteComponent noteComponent) implements CustomPayload {
	public static final Id<C2SEditNoteItem> ID = new Id<>(Glowcase.id("channel.note_item"));

	public static final PacketCodec<RegistryByteBuf, C2SEditNoteItem> PACKET_CODEC = PacketCodec.tuple(
		NoteComponent.TYPE.getPacketCodec(), C2SEditNoteItem::noteComponent,
		C2SEditNoteItem::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	// TODO: Client and Server side trimming

	public void receive(ServerPlayNetworking.Context context) {
		ItemStack stack = context.player().getMainHandStack();
		if (!(stack.isOf(Glowcase.NOTE_ITEM.get()))) return;

		if (stack.contains(Glowcase.NOTE_COMPONENT.get())) {
			NoteComponent existingNote = stack.get(Glowcase.NOTE_COMPONENT.get());
			assert existingNote != null;
			if (existingNote.title().isPresent())
				return; // Already signed; This copy must not be modified
		}

		stack.set(Glowcase.NOTE_COMPONENT.get(), noteComponent);
	}

	public void send() {
		ClientPlayNetworking.send(this);
	}
}
