package dev.hephaestus.glowcase.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record NoteComponent(List<Text> lines, Alignment alignment, Optional<String> title, Optional<String> author) {
	public static final int LINES_LIMIT = 10;
	public static final int LINE_LIMIT = 105;
	public static final int TITLE_LIMIT = 32;

	public static final Codec<NoteComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			TextCodecs.CODEC.sizeLimitedListOf(LINES_LIMIT).fieldOf("lines").forGetter(NoteComponent::lines),
			Codec.BYTE.fieldOf("alignment").forGetter((note) -> (byte) note.alignment.ordinal()),
			Codec.STRING.optionalFieldOf("title").forGetter(NoteComponent::title),
			Codec.STRING.optionalFieldOf("author").forGetter(NoteComponent::author)
		).apply(instance, (lines, alignment, title, author)
			-> new NoteComponent(lines, Alignment.values()[alignment], title, author))
	);

	public static final ComponentType<NoteComponent> TYPE = ComponentType.<NoteComponent>builder()
		.codec(CODEC)
		.packetCodec(PacketCodecs.registryCodec(CODEC))
		.build();

	public enum Alignment {
		LEFT, CENTER, RIGHT
	}
}
