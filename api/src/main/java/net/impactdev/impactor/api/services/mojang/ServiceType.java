package net.impactdev.impactor.api.services.mojang;

import java.util.Arrays;

/**
 * This enum represents the various portions of the Mojang API.
 */
public enum ServiceType {
	MINECRAFT_NET("minecraft.net", "minecraft.net"),
	SESSION_MINECRAFT_NET("session.minecraft.net", "Sessions"),
	ACCOUNT_MOJANG_COM("account.mojang.com", "Account Services"),
	AUTHSERVER_MOJANG_COM("authserver.mojang.com", "Auth Server"),
	SESSIONSERVER_MOJANG_COM("sessionserver.mojang.com", "Sessions Server"),
	API_MOJANG_COM("api.mojang.com", "Mojang API"),
	TEXTURES_MINECRAFT_NET("textures.minecraft.net", "Skins"),
	MOJANG_COM("mojang.com", "mojang.com");

	private final String path;
	private final String display;

	ServiceType(String path, String display) {
		this.path = path;
		this.display = display;
	}

	public static ServiceType from(String name) {
		return Arrays.stream(values()).filter(st -> st.path.equalsIgnoreCase(name)).findAny().get();
	}

	/**
	 * <p>This method overrides {@code java.lang.Object.toString()} and returns the address of the mojang api portion a certain enum constant represents.
	 * <p><strong>Example:</strong>
	 * {@code org.shanerx.mojang.Mojang.ServiceType.MINECRAFT_NET.toString()} will return {@literal minecraft.net}
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return name().toLowerCase().replace("_", ".");
	}

	public String getPath() {
		return this.path;
	}

	public String getDisplay() {
		return this.display;
	}
}
