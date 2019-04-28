package com.nickimpact.impactor.sponge.services;

import com.nickimpact.impactor.api.services.mojang.MojangServerStatusService;
import com.nickimpact.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

public class SpongeMojangServerStatusService extends MojangServerStatusService {
	@Override
	public void run() {
		Sponge.getScheduler().createTaskBuilder().execute(() -> {
			try {
				this.checker.fetch();
			} catch (Exception e) {
				SpongeImpactorPlugin.getInstance().getPluginLogger().warn("Failed to read Mojang Server Status with reason (" + e.getMessage() + ")");
			}
		}).async().interval(30, TimeUnit.SECONDS).submit(SpongeImpactorPlugin.getInstance());
	}
}
