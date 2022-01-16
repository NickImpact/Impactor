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

package net.impactdev.impactor.sponge;

import com.google.common.collect.*;
import com.google.inject.Inject;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.gui.signs.SignQuery;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.components.Depending;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.placeholders.PlaceholderSourcesImpl;
import net.impactdev.impactor.sponge.api.SpongeImpactorAPIProvider;
import net.impactdev.impactor.sponge.commands.PlaceholdersCommand;
import net.impactdev.impactor.sponge.configuration.ConfigKeys;
import net.impactdev.impactor.sponge.configuration.SpongeConfig;
import net.impactdev.impactor.sponge.configuration.SpongeConfigAdapter;
import net.impactdev.impactor.sponge.event.SpongeEventBus;
import net.impactdev.impactor.sponge.plugin.AbstractSpongePlugin;
import net.impactdev.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import net.impactdev.impactor.sponge.scoreboard.ScoreboardModule;
import net.impactdev.impactor.sponge.services.SpongeMojangServerStatusService;
import net.impactdev.impactor.sponge.text.SpongeMessageService;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import net.impactdev.impactor.sponge.ui.signs.SpongeSignQuery;
import net.impactdev.impactor.sponge.util.ScoreboardTesting;
import net.impactdev.impactor.sponge.util.SpongeClassPathAppender;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin("impactor")
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Depending {

	private static SpongeImpactorPlugin instance;

	private SpongeMojangServerStatusService mojangServerStatusService;

	private final PluginContainer pluginContainer;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Config config;

	@Inject
	public SpongeImpactorPlugin(PluginContainer container, Logger fallback) {
		super(PluginMetadata.builder().id("impactor").name("Impactor").version("@version@").build(), fallback);
		this.pluginContainer = container;
		ApiRegistrationUtil.register(new SpongeImpactorAPIProvider(new SpongeSchedulerAdapter(this, Sponge.game())));
	}

	public static SpongeImpactorPlugin getInstance() {
		return SpongeImpactorPlugin.instance;
	}

	@Listener(order = Order.FIRST)
	public void onConstruct(ConstructPluginEvent e) {
		instance = this;

		Impactor.getInstance().getRegistry().register(ImpactorPlugin.class, this);
		Impactor.getInstance().getRegistry().register(ClassPathAppender.class, new SpongeClassPathAppender(this));
		Impactor.getInstance().getRegistry().register(DependencyManager.class, new DependencyManager(this));

		this.getPluginLogger().info("Pooling plugin dependencies...");
		List<Dependency> toLaunch = Lists.newArrayList();
		for(ImpactorPlugin plugin : PluginRegistry.getAll()) {
			if(plugin instanceof Depending dependable) {

				for(Dependency dependency : dependable.getAllDependencies()) {
					if(toLaunch.contains(dependency)) {
						continue;
					}

					toLaunch.add(dependency);
				}

				for(Dependency storage : this.getDependencyManager().getRegistry().resolveStorageDependencies(Sets.newHashSet(dependable.getStorageRequirements()))) {
					if(toLaunch.contains(storage)) {
						continue;
					}

					toLaunch.add(storage);
				}
			}
		}

		this.getPluginLogger().info("Dependencies found, setting these up now...");
		this.getPluginLogger().info("Initializing default dependencies...");
		this.getDependencyManager().loadDependencies(EnumSet.of(Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_HOCON, Dependency.HOCON_CONFIG, Dependency.CONFIGURATE_GSON, Dependency.CONFIGURATE_YAML));
		this.getDependencyManager().loadDependencies(new HashSet<>(toLaunch));

		Impactor.getInstance().getRegistry().register(MessageService.class, new SpongeMessageService());
		Impactor.getInstance().getRegistry().registerBuilderSupplier(SignQuery.SignQueryBuilder.class, SpongeSignQuery.SpongeSignQueryBuilder::new);
		Impactor.getInstance().getRegistry().register(SpongePlaceholderManager.class, new SpongePlaceholderManager());
		Impactor.getInstance().getRegistry().registerBuilderSupplier(PlaceholderSources.SourceBuilder.class, PlaceholderSourcesImpl.PlaceholderSourcesBuilderImpl::new);

		new ScoreboardModule().initialize();

		Impactor.getInstance().getRegistry().register(EventBus.class, new SpongeEventBus());
		((SpongeEventBus)Impactor.getInstance().getEventBus()).enable();
	}

	@Listener
	public void onInit(StartingEngineEvent<Server> e) {
		this.config = new SpongeConfig(new SpongeConfigAdapter(this, new File(configDir.toFile(), "settings.conf")), new ConfigKeys());

		if(this.config.get(ConfigKeys.USE_MOJANG_STATUS_FETCHER)) {
			this.getPluginLogger().info("Enabling Mojang Status Watcher...");
			mojangServerStatusService = new SpongeMojangServerStatusService();
		}
	}

	@Listener
	public void whenCommandRegistration(RegisterCommandEvent<Command.Parameterized> event) {
		event.register(this.getPluginContainer(), new PlaceholdersCommand().create(), "placeholders");
	}

	@Listener
	public void onServerStart(StartedEngineEvent<Server> event) {
		Sponge.eventManager().registerListeners(this.getPluginContainer(), new ScoreboardTesting());

		ImmutableList<PlaceholderParser> parsers = Impactor.getInstance().getRegistry().get(SpongePlaceholderManager.class).getAllPlatformParsers();
		this.getPluginLogger().info("&eAvailable Placeholders:");
		Multimap<String, ResourceKey> sorted = ArrayListMultimap.create();
		Pattern pattern = Pattern.compile("(.+):(.+)");

		parsers.stream()
				.map(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER))
				.sorted(Comparator.comparing(ResourceKey::formatted))
				.forEach(parser -> {
					Matcher matcher = pattern.matcher(parser.formatted());
					if(matcher.find()) {
						Optional<PluginContainer> container = Sponge.pluginManager().plugin(matcher.group(1));
						sorted.put(container.map(c -> c.metadata().name().orElse("Unknown")).orElse("Custom"), parser);
					} else {
						sorted.put("Custom", parser);
					}
				});

		sorted.keySet().stream().sorted((s1, s2) -> {
			if(s1.equals("Custom")) {
				return 1;
			} else if(s2.equals("Custom")) {
				return -1;
			} else {
				return s1.compareTo(s2);
			}
		}).forEach(key -> {
			this.getPluginLogger().info("&3" + key);
			for(ResourceKey parser : sorted.get(key)) {
				this.getPluginLogger().info("&a- " + parser.formatted());
			}
		});
	}

	@Listener
	public void onShutdown(StoppingEngineEvent<Server> event) {
		((SpongeEventBus)Impactor.getInstance().getEventBus()).disable();
	}

	@Listener
	public void onGlobalRegistryValueRegistrationEvent(final RegisterRegistryValueEvent.GameScoped event) {
		final RegisterRegistryValueEvent.RegistryStep<PlaceholderParser> placeholderParserRegistryStep =
				event.registry(RegistryTypes.PLACEHOLDER_PARSER);
		new SpongePlaceholderManager().getAllInternalParsers().forEach(metadata -> {
			placeholderParserRegistryStep.register(ResourceKey.of(this.pluginContainer, metadata.getToken()), metadata.getParser());
		});
	}

	public Config getConfig() {
		return this.config;
	}

	public DependencyManager getDependencyManager() {
		return Impactor.getInstance().getRegistry().get(DependencyManager.class);
	}

	@Override
	public List<Dependency> getAllDependencies() {
		return ImmutableList.copyOf(Lists.newArrayList(
				Dependency.KYORI_EVENT,
				Dependency.KYORI_EVENT_METHOD,
				Dependency.KYORI_EVENT_METHOD_ASM,
				Dependency.KYORI_EXAMINATION,
				Dependency.KYORI_EXAMINATION_STRING,
				Dependency.KYORI_TEXT,
				Dependency.KYORI_TEXT_SERIALIZER_GSON,
				Dependency.KYORI_TEXT_SERIALIZER_LEGACY,
				Dependency.BYTEBUDDY,
				Dependency.FLOW_MATH
		));
	}

	@Override
	public List<StorageType> getStorageRequirements() {
		return Lists.newArrayList();
	}

	public SpongeMojangServerStatusService getMojangServerStatusService() {
		return this.mojangServerStatusService;
	}

	public PluginContainer getPluginContainer() {
		return this.pluginContainer;
	}
}
