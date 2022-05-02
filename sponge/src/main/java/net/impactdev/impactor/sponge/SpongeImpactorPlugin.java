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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.plugin.InternalImpactorPlugin;
import net.impactdev.impactor.sponge.api.SpongeImpactorAPIProvider;
import net.impactdev.impactor.sponge.commands.DevCommand;
import net.impactdev.impactor.sponge.commands.PlaceholdersCommand;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class SpongeImpactorPlugin extends InternalImpactorPlugin {

    private final SpongeImpactorBootstrap bootstrap;

    public static SpongeImpactorPlugin instance() {
        return (SpongeImpactorPlugin) Impactor.getInstance().getRegistry().get(InternalImpactorPlugin.class);
    }

    public SpongeImpactorPlugin(SpongeImpactorBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.register();
        ApiRegistrationUtil.register(new SpongeImpactorAPIProvider(this.bootstrap.scheduler()));
    }

    @Override
    public PluginLogger logger() {
        return this.bootstrap.logger();
    }

    @Override
    public void construct() {
        Registry registry = Impactor.getInstance().getRegistry();
        registry.register(InternalImpactorPlugin.class, this);
        registry.register(ImpactorPlugin.class, this); // TODO - Temporary

        this.download();
        this.modules();
        this.listeners();
    }

    @Override
    public void shutdown() {}

    @Override
    public SpongeImpactorBootstrap bootstrapper() {
        return this.bootstrap;
    }

    @Override
    public void listeners() {
        this.commands();
        this.placeholders();
    }

    @Override
    public void commands() {
        this.bootstrap.registerListener(new RegisterCommandsListener(this.bootstrap.container()));
    }

    public static final class RegisterCommandsListener {

        private final PluginContainer container;

        public RegisterCommandsListener(PluginContainer container) {
            this.container = container;
        }

        @Listener
        public void whenCommandRegistration(RegisterCommandEvent<Command.Parameterized> event) {
            event.register(this.container, new PlaceholdersCommand().create(), "placeholders");
            event.register(this.container, new DevCommand().create(), "dev");
        }

    }

    @Override
    public void placeholders() {
        this.bootstrap.registerListener(new RegisterPlaceholdersListener(this.bootstrap.container()));
    }

    public static final class RegisterPlaceholdersListener {

        private final PluginContainer container;

        public RegisterPlaceholdersListener(PluginContainer container) {
            this.container = container;
        }

        @Listener
        public void onGlobalRegistryValueRegistrationEvent(final RegisterRegistryValueEvent.GameScoped event) {
            final RegisterRegistryValueEvent.RegistryStep<PlaceholderParser> placeholderParserRegistryStep =
                    event.registry(RegistryTypes.PLACEHOLDER_PARSER);
            new SpongePlaceholderManager().getAllInternalParsers().forEach(metadata -> {
                placeholderParserRegistryStep.register(ResourceKey.of(this.container, metadata.getToken()), metadata.getParser());
            });
        }

    }

    @Override
    public Set<Dependency> dependencies() {
        return ImmutableSet.copyOf(Lists.newArrayList(
                ProvidedDependencies.BYTEBUDDY,
                ProvidedDependencies.FLOW_MATH,
                ProvidedDependencies.CONFIGURATE_CORE,
                ProvidedDependencies.CONFIGURATE_HOCON,
                ProvidedDependencies.TYPESAFE_CONFIG,
                ProvidedDependencies.CONFIGURATE_GSON,
                ProvidedDependencies.CONFIGURATE_YAML,
                ProvidedDependencies.REFLECTIONS
        ));
    }

    @Override
    public Optional<Path> configDirectory() {
        return Optional.of(this.bootstrap.configDirectory());
    }
}
