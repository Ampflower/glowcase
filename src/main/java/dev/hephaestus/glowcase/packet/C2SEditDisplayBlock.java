package dev.hephaestus.glowcase.packet;

import dev.hephaestus.glowcase.block.entity.DisplayBlockEntity;
import dev.hephaestus.glowcase.util.DisplayBlockSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public interface C2SEditDisplayBlock {
	DisplayBlockSettings settings();

	default void receive(ServerWorld world, BlockEntity blockEntity) {
		if ((blockEntity instanceof DisplayBlockEntity be)) {
			be.loadSettings(settings());
		}
	}
}
