package net.impactdev.impactor.sponge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
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
import net.impactdev.impactor.sponge.api.SpongeImpactorAPIProvider;
import net.impactdev.impactor.sponge.configuration.ConfigKeys;
import net.impactdev.impactor.sponge.configuration.SpongeConfig;
import net.impactdev.impactor.sponge.configuration.SpongeConfigAdapter;
import net.impactdev.impactor.sponge.event.SpongeEventBus;
import net.impactdev.impactor.sponge.plugin.AbstractSpongePlugin;
import net.impactdev.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import net.impactdev.impactor.sponge.services.SpongeMojangServerStatusService;
import net.impactdev.impactor.sponge.text.SpongeMessageService;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import net.impactdev.impactor.sponge.ui.SpongePage;
import net.impactdev.impactor.sponge.ui.icons.SpongeIcon;
import net.impactdev.impactor.sponge.ui.icons.SpongeIcons;
import net.impactdev.impactor.sponge.ui.SpongeLayout;
import net.impactdev.impactor.sponge.ui.signs.SpongeSignQuery;
import lombok.Getter;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.sponge.util.SpongeClassLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin("impactor")
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Depending {

	@Getter private static SpongeImpactorPlugin instance;

	@Getter private SpongeMojangServerStatusService mojangServerStatusService;

	@Getter
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

	@Listener(order = Order.FIRST)
	public void onConstruct(ConstructPluginEvent e) {
		instance = this;

		Impactor.getInstance().getRegistry().register(ImpactorPlugin.class, this);
		Impactor.getInstance().getRegistry().register(PluginClassLoader.class, new SpongeClassLoader(this));
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
	}

	@Listener
	public void onInit(StartingEngineEvent<Server> e) {
		this.config = new SpongeConfig(new SpongeConfigAdapter(this, new File(configDir.toFile(), "settings.conf")), new ConfigKeys());

		if(this.config.get(ConfigKeys.USE_MOJANG_STATUS_FETCHER)) {
			this.getPluginLogger().info("Enabling Mojang Status Watcher...");
			mojangServerStatusService = new SpongeMojangServerStatusService();
		}

		//Sponge.getServiceManager().provideUnchecked(ProtocolService.class).events().register(new SignListener());
	}

	@SuppressWarnings("unchecked")
	@Listener
	public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
		Impactor.getInstance().getScheduler().asyncLater(() -> {
			Impactor.getInstance().getScheduler().executeSync(() -> {
//				ViewableInventory test = ViewableInventory.builder()
//						.type(ContainerTypes.GENERIC_9X6)
//						.completeStructure().build();
//
//				SpongeLayout layout = SpongeLayout.builder()
//						.border()
//						.fill(SpongeIcon.builder()
//								.delegate(ItemStack.builder().itemType(ItemTypes.DIRT).build())
//								.listener((cause, container, clickType) -> {
//									this.getPluginLogger().info("Click detected");
//									return true;
//								})
//								.build()
//						)
//						.build();
//
//				SpongeUI.builder()
//						.view(test)
//						.title(Component.text("Testing").color(NamedTextColor.RED))
//						.build()
//						.define(layout)
//						.open(event.getPlayer());

				SpongePage<Integer> test = SpongePage.builder()
						.title(Component.text("Page Test").color(NamedTextColor.RED))
						.viewer(event.player())
						.view(SpongeLayout.builder().dimension(9, 5).border().dimension(9, 6).slots(SpongeIcons.BORDER, 45, 53).build())
						.offsets(new Vector2i(1, 1))
						.contentZone(new Vector2i(7, 3))
						.currentPage(ItemTypes.PAPER, 49)
						.nextPage(ItemTypes.ARROW, 50)
						.previousPage(ItemTypes.ARROW, 48)
						.build();
				test.applier(x -> SpongeIcon.builder()
						.delegate(ItemStack.builder()
								.itemType(ItemTypes.GOLD_NUGGET)
								.add(Keys.CUSTOM_NAME, Component.text(x).color(NamedTextColor.YELLOW))
								.build()
						)
						.listener((cause, container, clickType) -> {
							SpongeImpactorPlugin.getInstance().getPluginLogger().info("" + x);
							return true;
						})
						.build()
				);
				test.define(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47));
				test.open();
			});
		}, 10, TimeUnit.SECONDS);

		AtomicReference<Instant> prior = new AtomicReference<>(Instant.now());
		Sponge.server().scheduler().submit(Task.builder()
				.execute(() -> {
					Instant now = Instant.now();
					this.getPluginLogger().info("Timestamp: " + now + " - " + Duration.between(prior.get(), now).toMillis() + "ms since last iteration");
					prior.set(now);
				})
				.interval(Ticks.single())
				.plugin(this.getPluginContainer())
				.build()
		);
	}

	@Listener
	public void onServerStart(StartedEngineEvent<Server> event) {
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
						sorted.put(container.map(c -> c.getMetadata().getName().orElse("Unknown")).orElse("Custom"), parser);
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
