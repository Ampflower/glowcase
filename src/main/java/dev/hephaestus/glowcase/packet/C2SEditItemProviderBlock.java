package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.AbstractItemDisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.ItemProviderBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;

public record C2SEditItemProviderBlock(BlockPos pos, ItemProviderBlockEntity.GivesItem givesItem) implements C2SEditBlockEntity {
	public static final CustomPayload.Id<C2SEditItemProviderBlock> ID = new CustomPayload.Id<>(Glowcase.id("channel.item_provider"));
	public static final PacketCodec<RegistryByteBuf, C2SEditItemProviderBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditItemProviderBlock::pos,
		PacketCodecs.BYTE.xmap(index -> ItemProviderBlockEntity.GivesItem.values()[index], givesItem -> (byte) givesItem.ordinal()), C2SEditItemProviderBlock::givesItem,
		C2SEditItemProviderBlock::new
	);

	public static C2SEditItemProviderBlock of(ItemProviderBlockEntity be) {
		return new C2SEditItemProviderBlock(be.getPos(), be.givesItem);
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof ItemProviderBlockEntity be)) return;
		be.givesItem = this.givesItem();

		be.markDirty();
		be.dispatch();
	}
}
