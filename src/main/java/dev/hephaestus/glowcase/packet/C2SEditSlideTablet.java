package dev.hephaestus.glowcase.packet;

import com.mojang.datafixers.util.Pair;
import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.ScreenBlockEntity;
import dev.hephaestus.glowcase.item.TabletItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;

public record C2SEditSlideTablet(int index, String url, String alt) implements CustomPayload {
	public static final Id<C2SEditSlideTablet> ID = new Id<>(Glowcase.id("channel.slide_tablet"));
	public static final PacketCodec<RegistryByteBuf, C2SEditSlideTablet> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, C2SEditSlideTablet::index,
		PacketCodecs.STRING, C2SEditSlideTablet::url,
		PacketCodecs.STRING, C2SEditSlideTablet::alt,
		C2SEditSlideTablet::new
	);

	public static C2SEditSlideTablet of(int index, String url, String alt) {
		Pair<String, String> trimmed = ScreenBlockEntity.trimStr(url, alt);
		return new C2SEditSlideTablet(index, trimmed.getFirst(), trimmed.getSecond());
	}

	public void receive(ServerPlayNetworking.Context context) {
		ItemStack stack = context.player().getMainHandStack();
		if (!(stack.isOf(Glowcase.TABLET_ITEM.get()))) return;

		Pair<String, String> trimmed = ScreenBlockEntity.trimStr(this.url, this.alt);
		Pair<String, String> slide = new Pair<>(trimmed.getFirst(), trimmed.getSecond());

		// We need a modifiable variant
		ArrayList<Pair<String, String>> slideshow = new ArrayList<>(stack.getOrDefault(Glowcase.SLIDESHOW_COMPONENT.get(), new ArrayList<>()));

		if (this.index > slideshow.size())
			return;
		else if (this.index == slideshow.size())
			slideshow.add(this.index, slide);
		else
			slideshow.set(this.index, slide);

		// Trim "gaps" to only allow one at most instead of many next to each other
		for (int i=0; i < slideshow.size()-1; i++) {
			Pair<String, String> current = slideshow.get(i);
			Pair<String, String> next = slideshow.get(i+1);

			// Is current and next a gap?
			if (current.getFirst().isEmpty() && current.getSecond().isEmpty() && next.getFirst().isEmpty() && next.getSecond().isEmpty()) {
				slideshow.remove(i+1);
				i--;
			}
		}

		stack.set(Glowcase.SLIDESHOW_COMPONENT.get(), slideshow);
	}

	public void send() {
		ClientPlayNetworking.send(this);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
