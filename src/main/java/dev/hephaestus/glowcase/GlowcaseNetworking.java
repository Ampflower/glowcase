package dev.hephaestus.glowcase;

import dev.hephaestus.glowcase.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class GlowcaseNetworking {
	public static void init() {
		PayloadTypeRegistry.playC2S().register(C2SEditHyperlinkBlock.ID, C2SEditHyperlinkBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditItemDisplayBlock.ID, C2SEditItemDisplayBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditTextBlock.ID, C2SEditTextBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditPopupBlock.ID, C2SEditPopupBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditSpriteBlock.ID, C2SEditSpriteBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditOutlineBlock.ID, C2SEditOutlineBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditParticleDisplayBlock.ID, C2SEditParticleDisplayBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditSoundBlock.ID, C2SEditSoundBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditItemAcceptorBlock.ID, C2SEditItemAcceptorBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditScreenBlock.ID, C2SEditScreenBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditSlideTablet.ID, C2SEditSlideTablet.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditItemProviderBlock.ID, C2SEditItemProviderBlock.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditTabletItem.ID, C2SEditTabletItem.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SEditNoteItem.ID, C2SEditNoteItem.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(C2SEditHyperlinkBlock.ID, C2SEditHyperlinkBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditItemDisplayBlock.ID, C2SEditItemDisplayBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditTextBlock.ID, C2SEditTextBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditPopupBlock.ID, C2SEditPopupBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditSpriteBlock.ID, C2SEditSpriteBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditOutlineBlock.ID, C2SEditOutlineBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditParticleDisplayBlock.ID, C2SEditParticleDisplayBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditSoundBlock.ID, C2SEditSoundBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditItemAcceptorBlock.ID, C2SEditItemAcceptorBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditScreenBlock.ID, C2SEditScreenBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditSlideTablet.ID, C2SEditSlideTablet::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditItemProviderBlock.ID, C2SEditItemProviderBlock::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditTabletItem.ID, C2SEditTabletItem::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SEditNoteItem.ID, C2SEditNoteItem::receive);
	}
}
