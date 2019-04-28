package com.nickimpact.impactor.api.services.mojang;

public enum ServiceStatus {
	RED,
	YELLOW,
	GREEN;

	public static ServiceStatus from(String status) {
		for(ServiceStatus ss : values()) {
			if(ss.name().toLowerCase().equals(status)) {
				return ss;
			}
		}

		return null;
	}
}
