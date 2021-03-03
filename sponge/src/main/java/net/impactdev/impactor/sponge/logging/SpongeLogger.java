package net.impactdev.impactor.sponge.logging;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

public class SpongeLogger implements Logger {

	private final ImpactorPlugin plugin;
	private final org.slf4j.Logger fallback;

	public SpongeLogger(ImpactorPlugin plugin, org.slf4j.Logger fallback) {
		this.plugin = plugin;
		this.fallback = fallback;
	}

	@Override
	public void noTag(String message) {
		if(Sponge.isServerAvailable()) {
			Sponge.getServer().getConsole().sendMessage(this.toText(message));
		} else {
			fallback.info(message);
		}
	}

	@Override
	public void noTag(List<String> message) {
		message.forEach(this::noTag);
	}

	@Override
	public void info(String message) {
		if(Sponge.isServerAvailable()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.YELLOW, plugin.getMetadata().getName(), TextColors.GRAY, " \u00bb ", this.toText(message)));
		} else {
			fallback.info(message);
		}
	}

	@Override
	public void info(List<String> message) {
		message.forEach(this::info);
	}

	@Override
	public void warn(String message) {
		if(Sponge.isServerAvailable()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.YELLOW, plugin.getMetadata().getName(), TextColors.GRAY, " (", TextColors.GOLD, "Warning", TextColors.GRAY, ") ", this.toText(message)));
		} else {
			fallback.warn(message);
		}
	}

	@Override
	public void warn(List<String> message) {
		message.forEach(this::warn);
	}

	@Override
	public void error(String message) {
		if(Sponge.isServerAvailable()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.YELLOW, plugin.getMetadata().getName(), TextColors.GRAY, " (", TextColors.RED, "Error", TextColors.GRAY, ") ", this.toText(message)));
		} else {
			fallback.warn(message);
		}
	}

	@Override
	public void error(List<String> message) {
		message.forEach(this::error);
	}

	@Override
	public void debug(String message) {
		if(this.plugin.inDebugMode()) {
			if (Sponge.isServerAvailable()) {
				Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.YELLOW, plugin.getMetadata().getName(), TextColors.GRAY, " (", TextColors.AQUA, "Debug", TextColors.GRAY, ") ", this.toText(message)));
			} else {
				fallback.warn(message);
			}
		}
	}

	@Override
	public void debug(List<String> message) {
		if(this.plugin.inDebugMode()) {
			message.forEach(this::debug);
		}
	}

	private Text toText(String message) {
		return TextSerializers.FORMATTING_CODE.deserialize(message);
	}
}
