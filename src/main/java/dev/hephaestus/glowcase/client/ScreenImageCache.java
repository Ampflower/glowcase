package dev.hephaestus.glowcase.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ScreenImageCache {
	public static class ScreenTexture {
		private final CompletableFuture<Integer> loader;
		@Nullable
		private Identifier texture;

		private int width = 0;
		private int height = 0;

		public ScreenTexture(String raw_url) {
			loader = CompletableFuture.supplyAsync(() -> {
				// Ensure valid URL
				URL url;
				try {
					url = new URI(raw_url).toURL();
				} catch (Exception e) {
					return 900; // Not a valid URL
				}

				String protocol = url.getProtocol();
				if (!(protocol.equals("http") || protocol.equals("https"))) {
					return 901; // Invalid protocol, only HTTP and HTTPS are supported
				}

				// Fetch URL
				HttpURLConnection connection;
				InputStream stream;
				try {
					connection = (HttpURLConnection) url.openConnection(MinecraftClient.getInstance().getNetworkProxy());
					connection.setDoInput(true);
					connection.setDoOutput(false);
					connection.connect();

					int status = connection.getResponseCode();
					if (status/100 != 2)
						return status; // An actual status code for once here

					stream = connection.getInputStream();
				} catch (SocketTimeoutException e){
					return 408; // Request Timeout
				} catch (Exception e) {
					return 902; // Unable to create a connection
				}

				// Parse image
				NativeImage nativeImage;
				try {
					nativeImage = NativeImage.read(stream);
				} catch (IOException e) {
					return 903; // Unable to parse image.
				}

				// TODO: GIF support? STBImage _should_ support gif, but I didn't manage to make it work yet

				// TODO: Perhaps adding a local file cache might be wise

				int result = MinecraftClient.getInstance().submit(() -> {
					width = nativeImage.getWidth();
					height = nativeImage.getHeight();

					NativeImageBackedTexture nativeTexture = new NativeImageBackedTexture(nativeImage);

					// Register image as texture
					TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
					this.texture = textureManager.registerDynamicTexture("glowcase/img", nativeTexture);

					return 200;
				}).join();

				connection.disconnect();
				return result;
			}, Util.getMainWorkerExecutor());
		}

		public Pair<Integer, Identifier> getTexture() {
			if (loader.isDone())
				return new Pair<>(loader.join(), texture);

			return new Pair<>(102, texture);
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	private final HashMap<String, ScreenTexture> cache = new HashMap<>();

	public ScreenTexture getImage(String url) {
		if (!cache.containsKey(url))
			cache.put(url, new ScreenTexture(url));

		return cache.get(url);
	}
}
