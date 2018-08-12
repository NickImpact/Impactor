package com.nickimpact.impactor.api.services.placeholders;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class PlaceholderAPIService implements PlaceholderService<TextTemplate> {

	private final SpongePlugin plugin;
	private me.rojo8399.placeholderapi.PlaceholderService service;

	public PlaceholderAPIService(SpongePlugin plugin) {
		this.plugin = plugin;
		this.service = Sponge.getServiceManager().provideUnchecked(me.rojo8399.placeholderapi.PlaceholderService.class);
	}

	@Override
	public Text parse(String template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return this.service.replacePlaceholders(template, source, null);
	}

	@Override
	public List<Text> parse(Collection<String> templates, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		List<Text> out = Lists.newArrayList();
		for(String template : templates) {
			out.add(this.parse(template, source, tokens, variables));
		}

		return out;
	}

	@Override
	public Text parse(TextTemplate template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return this.service.replacePlaceholders(template, source, null);
	}
}
