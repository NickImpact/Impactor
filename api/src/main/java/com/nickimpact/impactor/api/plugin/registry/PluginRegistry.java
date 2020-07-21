package com.nickimpact.impactor.api.plugin.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;

import java.util.Map;
import java.util.Optional;

/**
 *
 */
public final class PluginRegistry {

    /** A mapping of a plugins ID to its actual implementation */
    private static final Map<String, ImpactorPlugin> plugins = Maps.newHashMap();

    public static void register(ImpactorPlugin plugin) {
        plugins.put(plugin.getMetadata().getID(), plugin);
    }

    public static Optional<ImpactorPlugin> get(String id) {
        return Optional.ofNullable(plugins.get(id));
    }

    public static ImmutableList<ImpactorPlugin> getAll() {
        return ImmutableList.copyOf(plugins.values());
    }

    public static boolean isRegistered(String id) {
        return plugins.containsKey(id);
    }

}
