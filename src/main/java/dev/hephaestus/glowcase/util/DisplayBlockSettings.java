package dev.hephaestus.glowcase.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public record DisplayBlockSettings(Vector3f offset, Vector3f scale, float pitch, float yaw) {
	public static final Codec<DisplayBlockSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codecs.VECTOR_3F.optionalFieldOf("offset", new Vector3f()).forGetter(DisplayBlockSettings::offset),
		Codecs.VECTOR_3F.optionalFieldOf("scale", new Vector3f(1.0F)).forGetter(DisplayBlockSettings::scale),
		Codec.FLOAT.optionalFieldOf("pitch", 0F).forGetter(DisplayBlockSettings::pitch),
		Codec.FLOAT.optionalFieldOf("offset", 0F).forGetter(DisplayBlockSettings::yaw)
	).apply(instance, DisplayBlockSettings::new));

	public static final PacketCodec<ByteBuf, DisplayBlockSettings> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VECTOR3F, DisplayBlockSettings::offset,
		PacketCodecs.VECTOR3F, DisplayBlockSettings::scale,
		PacketCodecs.FLOAT, DisplayBlockSettings::pitch,
		PacketCodecs.FLOAT, DisplayBlockSettings::yaw,
		DisplayBlockSettings::new
	);

	public boolean isEmpty() {
		return offset.equals(new Vector3f()) && scale.equals(new Vector3f(1.0F)) && pitch == 0F && yaw == 0F;
	}
}
