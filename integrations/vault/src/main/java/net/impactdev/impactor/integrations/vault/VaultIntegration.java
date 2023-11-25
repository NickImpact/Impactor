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

package net.impactdev.impactor.integrations.vault;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.economy.events.SuggestEconomyServiceEvent;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.events.ImpactorEventBus;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.kyori.event.EventBus;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public final class VaultIntegration extends JavaPlugin {

    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor-vault-integration")
            .name("Impactor Vault Integration")
            .version("@version@")
            .build();

    private Config config;

    @Override
    public void onLoad() {
        this.config = Config.builder()
                .path(Paths.get("config", "impactor", "integrations", "vault.conf"))
                .provider(VaultConfig.class)
                .build();

        if(this.config.get(VaultConfig.USE_VAULT)) {
            VaultService service = new VaultService();
            EventBus<ImpactorEvent> bus = ImpactorEventBus.bus();
            bus.subscribe(SuggestEconomyServiceEvent.class, event -> {
                event.suggest(this.metadata, () -> service, 1);
            });
            bus.subscribe(VaultService.VaultReadyEvent.class, event -> service.vault(event.economy(), this.config));
        } else {

        }
    }

    @Override
    public void onEnable() {
        if(this.config.get(VaultConfig.USE_VAULT)) {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp == null) {
                this.getLogger().severe("Could not locate a valid vault service...");
            } else {
                ImpactorEventBus.bus().post(new VaultService.VaultReadyEvent(rsp.getProvider()));
            }
        } else {

        }
    }

}
