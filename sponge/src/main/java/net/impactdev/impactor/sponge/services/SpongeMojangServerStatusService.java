package net.impactdev.impactor.sponge.services;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.mojang.MojangServerStatusService;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class SpongeMojangServerStatusService extends MojangServerStatusService {
	@Override
	public void run() {
		Impactor.getInstance().getScheduler().asyncRepeating(() -> {
			try {
				this.checker.fetch();
			} catch (Exception e) {
				SpongeImpactorPlugin.getInstance().getPluginLogger().warn("Failed to read Mojang Server Status with reason (" + e.getMessage() + ")");
			}
		}, 30, TimeUnit.SECONDS);
	}
}
