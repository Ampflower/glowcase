package dev.hephaestus.glowcase;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedName;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;

public class GlowcaseConfig extends ReflectiveConfig {
	@Comment("Logs what, why and where a screen could not fetch an image, if enabled.")
	@SerializedName("log_invalid_screens")
	public final TrackedValue<Boolean> logInvalidScreens = value(false);

	@Comment("List of specifically allowed domains or wildcards. Overrules the blacklist.")
	public final TrackedValue<ValueList<String>> whitelist = list("");

	@Comment("List of specifically disallowed domains or wildcards. Overruled by the whitelist.")
	public final TrackedValue<ValueList<String>> blacklist = list("",
		"localhost"
	);
}
