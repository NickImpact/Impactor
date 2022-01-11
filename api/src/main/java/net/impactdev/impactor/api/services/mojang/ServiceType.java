/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

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
