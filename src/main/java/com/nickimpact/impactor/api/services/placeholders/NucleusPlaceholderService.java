package com.nickimpact.impactor.api.services.placeholders;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class NucleusPlaceholderService implements PlaceholderService<NucleusTextTemplate> {

	private final SpongePlugin plugin;

	private NucleusTextTemplate getTemplate(String text) throws Exception {
		return NucleusAPI.getMessageTokenService().createFromString(text);
	}

	private List<NucleusTextTemplate> getTemplates(List<String> texts) throws Exception {
		List<NucleusTextTemplate> templates = Lists.newArrayList();
		for(String str : texts) {
			templates.add(getTemplate(str));
		}

		return templates;
	}

	@Override
	public Text parse(String template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		try {
			return this.parse(this.getTemplate(template), source, tokens, variables);
		} catch (Exception e) {
			return Text.of(this.plugin.getPluginInfo().error(), "Unable to parse template: ", template);
		}
	}

	@Override
	public List<Text> parse(Collection<String> templates, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		List<Text> out = Lists.newArrayList();
		for(String template : templates) {
			try {
				out.add(this.getTemplate(template).getForCommandSource(source, tokens, variables));
			} catch (Exception e) {
				out.add(Text.of(this.plugin.getPluginInfo().error(), "Unable to parse template: ", template));
			}
		}

		return out;
	}

	@Override
	public Text parse(NucleusTextTemplate template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return template.getForCommandSource(source, tokens, variables);
	}
}
