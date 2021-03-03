package net.impactdev.impactor.sponge.services;

import net.impactdev.impactor.api.services.mojang.MojangServerStatusService;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
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
