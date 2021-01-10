package net.impactdev.impactor.sponge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.ichorpowered.protocolcontrol.service.ProtocolService;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.gui.signs.SignQuery;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.components.Depending;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.classloader.PluginClassLoader;
import net.impactdev.impactor.api.dependencies.classloader.ReflectionClassLoader;
import net.impactdev.impactor.sponge.api.SpongeImpactorAPIProvider;
import net.impactdev.impactor.sponge.configuration.ConfigKeys;
import net.impactdev.impactor.sponge.configuration.SpongeConfig;
import net.impactdev.impactor.sponge.configuration.SpongeConfigAdapter;
import net.impactdev.impactor.sponge.event.SpongeEventBus;
import net.impactdev.impactor.sponge.listeners.SignListener;
import net.impactdev.impactor.sponge.plugin.AbstractSpongePlugin;
import net.impactdev.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import net.impactdev.impactor.sponge.services.SpongeMojangServerStatusService;
import net.impactdev.impactor.sponge.text.SpongeMessageService;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import net.impactdev.impactor.sponge.text.placeholders.provided.tick.TPSWatcher;
import net.impactdev.impactor.sponge.ui.signs.SpongeSignQuery;
import lombok.Getter;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;
import org.spongepowered.api.text.placeholder.PlaceholderParser;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(id = "impactor",
		name = "ImpactorAPI",
		version = "@version@",
		description = "A universal API for multiple tools for development"
)
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Depending {

	@Getter private static SpongeImpactorPlugin instance;

	@Getter private TPSWatcher watcher;

	@Getter private SpongeMojangServerStatusService mojangServerStatusService;

	@Inject
	@Getter
	private PluginContainer pluginContainer;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Config config;

	@Inject
	public SpongeImpactorPlugin(Logger fallback, @SynchronousExecutor SpongeExecutorService sync, @AsynchronousExecutor SpongeExecutorService async) {
		super(PluginMetadata.builder().id("impactor").name("Impactor").version("@version@").build(), fallback);
		ApiRegistrationUtil.register(new SpongeImpactorAPIProvider(SpongeSchedulerAdapter.builder()
				.bootstrap(this)
				.sync(sync)
				.async(async)
				.scheduler(Sponge.getScheduler())
				.build()
		));
	}

	@Listener(order = Order.FIRST)
	public void onPreInit(GamePreInitializationEvent e) {
		instance = this;

		Impactor.getInstance().getRegistry().register(ImpactorPlugin.class, this);
		Impactor.getInstance().getRegistry().register(PluginClassLoader.class, new ReflectionClassLoader(this));
		Impactor.getInstance().getRegistry().register(DependencyManager.class, new DependencyManager(this));

		this.getPluginLogger().info("Pooling plugin dependencies...");
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
		Impactor.getInstance().getRegistry().register(EventBus.class, new SpongeEventBus());
		((SpongeEventBus)Impactor.getInstance().getEventBus()).enable();
		this.watcher = new TPSWatcher(false);
	}

	@Listener
	public void onInit(GameInitializationEvent e) {
		this.config = new SpongeConfig(new SpongeConfigAdapter(this, new File(configDir.toFile(), "settings.conf")), new ConfigKeys());

		if(this.config.get(ConfigKeys.USE_MOJANG_STATUS_FETCHER)) {
			this.getPluginLogger().info("Enabling Mojang Status Watcher...");
			mojangServerStatusService = new SpongeMojangServerStatusService();
		}

		Sponge.getServiceManager().provideUnchecked(ProtocolService.class).events().register(new SignListener());
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		ImmutableList<PlaceholderParser> parsers = Impactor.getInstance().getRegistry().get(SpongePlaceholderManager.class).getAllPlatformParsers();
		this.getPluginLogger().info("&eAvailable Placeholders:");
		Multimap<String, PlaceholderParser> sorted = ArrayListMultimap.create();
		Pattern pattern = Pattern.compile("(.+):(.+)");

		parsers.stream().sorted(Comparator.comparing(PlaceholderParser::getId)).forEach(parser -> {
			Matcher matcher = pattern.matcher(parser.getId());
			if(matcher.find()) {
				Optional<PluginContainer> container = Sponge.getPluginManager().getPlugin(matcher.group(1));
				sorted.put(container.map(PluginContainer::getName).orElse("Custom"), parser);
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
			for(PlaceholderParser parser : sorted.get(key)) {
				this.getPluginLogger().info("&a- " + parser.getId() + " : " + parser.getName());
			}
		});
	}

	@Listener
	public void onShutdown(GameStoppedServerEvent event) {
		((SpongeEventBus)Impactor.getInstance().getEventBus()).disable();
	}

	@Listener
	public void onPlaceholderRegistryEvent(GameRegistryEvent.Register<PlaceholderParser> event) {
		for(PlaceholderParser parser : Impactor.getInstance().getRegistry().get(SpongePlaceholderManager.class).getAllInternalParsers()) {
			event.register(parser);
		}
	}

	public Config getConfig() {
		return this.config;
	}

	public PluginClassLoader getPluginClassLoader() {
		return Impactor.getInstance().getRegistry().get(PluginClassLoader.class);
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
}
