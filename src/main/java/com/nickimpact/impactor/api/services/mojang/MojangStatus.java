package com.nickimpact.impactor.api.services.mojang;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.ImpactorCore;
import org.shanerx.mojang.Mojang;
import org.spongepowered.api.Sponge;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MojangStatus {

	private Mojang mojang;
	private Map<Mojang.ServiceType, Mojang.ServiceStatus> statuses;

	public MojangStatus() {
		this.mojang = new Mojang().connect();
		this.statuses = Maps.newHashMap();
		Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
			for(Mojang.ServiceType type : Mojang.ServiceType.values()) {
				statuses.put(type, this.getCurrentStatus(type));
			}
		}).interval(30, TimeUnit.SECONDS).submit(ImpactorCore.getInstance());
	}

	public Mojang getMojang() {
		return mojang;
	}

	private Mojang.ServiceStatus getCurrentStatus(Mojang.ServiceType type) {
		return this.statuses.get(type);
	}

	public Mojang.ServiceStatus getStatus(Mojang.ServiceType type) {
		return this.mojang.getStatus(type);
	}
}
