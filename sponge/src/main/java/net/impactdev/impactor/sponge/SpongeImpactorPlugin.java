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
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.api.scoreboard.lines.types.RefreshingLine;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.RefreshingObjective;
import net.impactdev.impactor.api.ui.ImpactorUI;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.components.Depending;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.config.ConfigMaintainer;
import net.impactdev.impactor.common.dependencies.DependencyContainer;
import net.impactdev.impactor.common.event.ImpactorEventBus;
import net.impactdev.impactor.common.placeholders.PlaceholderSourcesImpl;
import net.impactdev.impactor.common.ui.LayoutImpl;
import net.impactdev.impactor.sponge.api.SpongeImpactorAPIProvider;
import net.impactdev.impactor.sponge.commands.PlaceholdersCommand;
import net.impactdev.impactor.sponge.plugin.AbstractSpongePlugin;
import net.impactdev.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import net.impactdev.impactor.sponge.scoreboard.SpongeScoreboard;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeConstantFrame;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeListeningFrame;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeRefreshingFrame;
import net.impactdev.impactor.sponge.scoreboard.lines.types.SpongeConstantLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeAnimatedLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeListeningLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeRefreshingLine;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeAnimatedObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeConstantObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeListeningObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeRefreshingObjective;
import net.impactdev.impactor.sponge.text.SpongeMessageService;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import net.impactdev.impactor.sponge.ui.containers.SpongePagination;
import net.impactdev.impactor.sponge.ui.containers.SpongeUI;
import net.impactdev.impactor.sponge.ui.containers.icons.SpongeIcon;
import net.impactdev.impactor.sponge.ui.signs.SpongeSignQuery;
import net.impactdev.impactor.sponge.util.SpongeClassPathAppender;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;
import java.util.*;

@Plugin("impactor")
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Depending {

	private static SpongeImpactorPlugin instance;
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

		Registry registry = Impactor.getInstance().getRegistry();
		registry.register(ImpactorPlugin.class, this);
		registry.registerBuilderSupplier(Config.ConfigBuilder.class, ConfigMaintainer.ConfigMaintainerBuilder::new);
		registry.register(ClassPathAppender.class, new SpongeClassPathAppender(this));
		registry.registerBuilderSupplier(Dependency.DependencyBuilder.class, DependencyContainer.DependencyContainerBuilder::new);
		registry.register(DependencyManager.class, new DependencyManager(this));

		this.getPluginLogger().info("Startup", "Pooling plugin dependencies...");
		List<Dependency> toLaunch = Lists.newArrayList();
		for(ImpactorPlugin plugin : PluginRegistry.getAll()) {
			if(plugin instanceof Depending) {
				Depending dependable = (Depending) plugin;
				for(Dependency dependency : dependable.getAllDependencies()) {
					if(toLaunch.contains(dependency)) {
						continue;
					}

					toLaunch.add(dependency);
				}

				for(Dependency storage : this.getDependencyManager().registry().resolveStorageDependencies(Sets.newHashSet(dependable.getStorageRequirements()))) {
					if(toLaunch.contains(storage)) {
						continue;
					}

					toLaunch.add(storage);
				}
			}
		}

		this.getPluginLogger().info("Startup", "Dependencies found, setting these up now...");
		this.getPluginLogger().info("Startup", "Initializing default dependencies...");
		this.getDependencyManager().loadDependencies(Lists.newArrayList(
				ProvidedDependencies.CONFIGURATE_CORE,
				ProvidedDependencies.CONFIGURATE_HOCON,
				ProvidedDependencies.TYPESAFE_CONFIG,
				ProvidedDependencies.CONFIGURATE_GSON,
				ProvidedDependencies.CONFIGURATE_YAML
		));
		this.getDependencyManager().loadDependencies(new HashSet<>(toLaunch));

		Impactor.getInstance().getRegistry().register(MessageService.class, new SpongeMessageService());
		Impactor.getInstance().getRegistry().registerBuilderSupplier(SignQuery.SignQueryBuilder.class, SpongeSignQuery.SpongeSignQueryBuilder::new);
		Impactor.getInstance().getRegistry().register(SpongePlaceholderManager.class, new SpongePlaceholderManager());
		Impactor.getInstance().getRegistry().registerBuilderSupplier(PlaceholderSources.SourceBuilder.class, PlaceholderSourcesImpl.PlaceholderSourcesBuilderImpl::new);

		registry.registerBuilderSupplier(Icon.IconBuilder.class, SpongeIcon.SpongeIconBuilder::new);
		registry.registerBuilderSupplier(Layout.LayoutBuilder.class, LayoutImpl.LayoutImplBuilder::new);
		registry.registerBuilderSupplier(Pagination.PaginationBuilder.class, SpongePagination.SpongePaginationBuilder::new);
		registry.registerBuilderSupplier(ImpactorUI.UIBuilder.class, SpongeUI.SpongeUIBuilder::new);

		// Scoreboard Registration
		registry.registerBuilderSupplier(ImpactorScoreboard.ScoreboardBuilder.class, SpongeScoreboard.SpongeScoreboardBuilder::new);

		// Objectives
		registry.registerBuilderSupplier(ConstantObjective.ConstantObjectiveBuilder.class, SpongeConstantObjective.SpongeConstantObjectiveBuilder::new);
		registry.registerBuilderSupplier(RefreshingObjective.RefreshingObjectiveBuilder.class, SpongeRefreshingObjective.SpongeRefreshingObjectiveBuilder::new);
		registry.registerBuilderSupplier(ListeningObjective.ListeningObjectiveBuilder.class, SpongeListeningObjective.SpongeListeningObjectiveBuilder::new);
		registry.registerBuilderSupplier(AnimatedObjective.AnimatedObjectiveBuilder.class, SpongeAnimatedObjective.SpongeAnimatedObjectiveBuilder::new);

		// Lines
		registry.registerBuilderSupplier(ConstantLine.ConstantLineBuilder.class, SpongeConstantLine.SpongeConstantLineBuilder::new);
		registry.registerBuilderSupplier(RefreshingLine.RefreshingLineBuilder.class, SpongeRefreshingLine.SpongeRefreshingLineBuilder::new);
		registry.registerBuilderSupplier(AnimatedLine.AnimatedBuilder.class, SpongeAnimatedLine.SpongeAnimatedBuilder::new);
		registry.registerBuilderSupplier(ListeningLine.ListeningBuilder.class, SpongeListeningLine.SpongeListeningLineBuilder::new);

		// Frames
		registry.registerBuilderSupplier(ConstantFrame.ConstantFrameBuilder.class, SpongeConstantFrame.SpongeConstantFrameBuilder::new);
		registry.registerBuilderSupplier(RefreshingFrame.RefreshingFrameBuilder.class, SpongeRefreshingFrame.SpongeRefreshingFrameBuilder::new);
		registry.registerBuilderSupplier(ListeningFrame.ListeningFrameBuilder.class, SpongeListeningFrame.SpongeListeningFrameBuilder::new);
		Impactor.getInstance().getRegistry().register(EventBus.class, new ImpactorEventBus());
		((ImpactorEventBus) Impactor.getInstance().getEventBus()).enable();
	}

	@Listener
	public void whenCommandRegistration(RegisterCommandEvent<Command.Parameterized> event) {
		event.register(this.getPluginContainer(), new PlaceholdersCommand().create(), "placeholders");
	}

//	@Listener
//	public void onServerStart(StartedEngineEvent<Server> event) {
//		ImmutableList<PlaceholderParser> parsers = Impactor.getInstance().getRegistry().get(SpongePlaceholderManager.class).getAllPlatformParsers();
//		this.getPluginLogger().info("&eAvailable Placeholders:");
//		Multimap<String, ResourceKey> sorted = ArrayListMultimap.create();
//		Pattern pattern = Pattern.compile("(.+):(.+)");
//
//		parsers.stream()
//				.map(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER))
//				.sorted(Comparator.comparing(ResourceKey::formatted))
//				.forEach(parser -> {
//					Matcher matcher = pattern.matcher(parser.formatted());
//					if(matcher.find()) {
//						Optional<PluginContainer> container = Sponge.pluginManager().plugin(matcher.group(1));
//						sorted.put(container.map(c -> c.metadata().name().orElse("Unknown")).orElse("Custom"), parser);
//					} else {
//						sorted.put("Custom", parser);
//					}
//				});
//

//	}

	@Listener
	public void onShutdown(StoppingEngineEvent<Server> event) {
		((ImpactorEventBus)Impactor.getInstance().getEventBus()).disable();
	}

	@Listener
	public void onGlobalRegistryValueRegistrationEvent(final RegisterRegistryValueEvent.GameScoped event) {
		final RegisterRegistryValueEvent.RegistryStep<PlaceholderParser> placeholderParserRegistryStep =
				event.registry(RegistryTypes.PLACEHOLDER_PARSER);
		new SpongePlaceholderManager().getAllInternalParsers().forEach(metadata -> {
			placeholderParserRegistryStep.register(ResourceKey.of(this.pluginContainer, metadata.getToken()), metadata.getParser());
		});
	}

	public Path getConfigDir() {
		return this.configDir;
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
				ProvidedDependencies.KYORI_EVENT_API,
				ProvidedDependencies.KYORI_EVENT_METHOD,
				ProvidedDependencies.KYORI_EVENT_METHOD_ASM,
				ProvidedDependencies.BYTEBUDDY,
				ProvidedDependencies.FLOW_MATH
		));
	}

	@Override
	public List<StorageType> getStorageRequirements() {
		return Lists.newArrayList();
	}

	public PluginContainer getPluginContainer() {
		return this.pluginContainer;
	}

	@Override
	public void construct() {

	}

	@Override
	public void enable() {

	}

	@Override
	public void disable() {

	}
}
