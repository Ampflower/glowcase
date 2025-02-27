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

public record C2SEditItemProviderBlock(BlockPos pos, AbstractItemDisplayBlockEntity.RotationType rotationType, ItemProviderBlockEntity.GivesItem givesItem, AbstractItemDisplayBlockEntity.Offset offset, C2SEditItemProviderBlock.ItemProviderBlockValues values) implements C2SEditBlockEntity {
	public static final CustomPayload.Id<C2SEditItemProviderBlock> ID = new CustomPayload.Id<>(Glowcase.id("channel.item_provider"));
	public static final PacketCodec<RegistryByteBuf, C2SEditItemProviderBlock> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, C2SEditItemProviderBlock::pos,
		PacketCodecs.BYTE.xmap(index -> AbstractItemDisplayBlockEntity.RotationType.values()[index], rotation -> (byte) rotation.ordinal()), C2SEditItemProviderBlock::rotationType,
		PacketCodecs.BYTE.xmap(index -> ItemProviderBlockEntity.GivesItem.values()[index], givesItem -> (byte) givesItem.ordinal()), C2SEditItemProviderBlock::givesItem,
		PacketCodecs.BYTE.xmap(index -> AbstractItemDisplayBlockEntity.Offset.values()[index], offset -> (byte) offset.ordinal()), C2SEditItemProviderBlock::offset,
		C2SEditItemProviderBlock.ItemProviderBlockValues.PACKET_CODEC, C2SEditItemProviderBlock::values,
		C2SEditItemProviderBlock::new
	);

	public static C2SEditItemProviderBlock of(ItemProviderBlockEntity be) {
		return new C2SEditItemProviderBlock(be.getPos(), be.rotationType, be.givesItem, be.offset, new C2SEditItemProviderBlock.ItemProviderBlockValues(be.getCachedState().get(Properties.ROTATION), be.showName, be.pitch, be.yaw));
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}

	@Override
	public void receive(ServerWorld world, BlockEntity blockEntity) {
		if (!(blockEntity instanceof ItemProviderBlockEntity be)) return;
		if (this.values().rotation() < 0 || this.values().rotation() >= RotationPropertyHelper.getMax()) return;

		be.givesItem = this.givesItem();
		be.rotationType = this.rotationType();
		be.offset = this.offset();
		be.pitch = this.values().pitch();
		be.yaw = this.values().yaw();
		be.showName = this.values().showName();

		world.setBlockState(this.pos(), world.getBlockState(this.pos()).with(Properties.ROTATION, this.values().rotation()));

		be.markDirty();
		be.dispatch();
	}

	// separated for tuple call
	public record ItemProviderBlockValues(int rotation, boolean showName, float pitch, float yaw) {
		public static final PacketCodec<RegistryByteBuf, C2SEditItemProviderBlock.ItemProviderBlockValues> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, C2SEditItemProviderBlock.ItemProviderBlockValues::rotation,
			PacketCodecs.BOOL, C2SEditItemProviderBlock.ItemProviderBlockValues::showName,
			PacketCodecs.FLOAT, C2SEditItemProviderBlock.ItemProviderBlockValues::pitch,
			PacketCodecs.FLOAT, C2SEditItemProviderBlock.ItemProviderBlockValues::yaw,
			C2SEditItemProviderBlock.ItemProviderBlockValues::new
		);
	}
}
