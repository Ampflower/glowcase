package dev.hephaestus.glowcase.client.util;

import dev.hephaestus.glowcase.Glowcase;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class NoteTextColorResource implements SynchronousResourceReloader, IdentifiableResourceReloadListener {
	private static final Identifier TEXTURE = Glowcase.id("textures/gui/note.png");

	public static int TXT_COLOR = 0x000000;

	@Override
	public void reload(ResourceManager manager) {
		Optional<Resource> resource = manager.getResource(TEXTURE);

		if (resource.isPresent()) {
			try {
				InputStream inputStream = resource.get().getInputStream();
				BufferedImage image = ImageIO.read(inputStream);

				TXT_COLOR = image.getRGB(image.getWidth()-1, image.getHeight()-1);
			} catch (IOException ignored) { }
		}
	}

	@Override
	public Identifier getFabricId() {
		return Glowcase.id("note_txt_color");
	}
}
