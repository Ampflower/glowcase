package dev.hephaestus.glowcase.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record NoteComponent(List<Text> lines, boolean horizontal, Optional<Text> title, Optional<String> author) {
	public static final int LINES_LIMIT = 16;
	public static final int LINE_LIMIT = 512;
	public static final int TITLE_LIMIT = 64;

	public static final Codec<NoteComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			TextCodecs.codec(LINE_LIMIT).sizeLimitedListOf(LINES_LIMIT).fieldOf("lines").forGetter(NoteComponent::lines),
			Codec.BOOL.fieldOf("horizontal").forGetter(NoteComponent::horizontal),
			TextCodecs.codec(TITLE_LIMIT).optionalFieldOf("title").forGetter(NoteComponent::title),
			Codec.STRING.optionalFieldOf("author").forGetter(NoteComponent::author)
		).apply(instance, NoteComponent::new)
	);

	public static final ComponentType<NoteComponent> TYPE = ComponentType.<NoteComponent>builder()
		.codec(CODEC)
		.packetCodec(PacketCodecs.registryCodec(CODEC))
		.build();
}
