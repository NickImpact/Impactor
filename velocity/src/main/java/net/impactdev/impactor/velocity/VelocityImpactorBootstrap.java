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

package net.impactdev.impactor.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.impactdev.impactor.api.logging.Logger;

@Plugin(id = "impactor", name = "Impactor", version = "@version@", authors = {"NickImpact"})
public class VelocityImpactorBootstrap {

    private static VelocityImpactorBootstrap instance;

    private final VelocityImpactorPlugin plugin;
    private final ProxyServer server;

    @Inject
    public VelocityImpactorBootstrap(ProxyServer server) {
        instance = this;
        this.server = server;
        this.plugin = new VelocityImpactorPlugin(this);
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        try {
            this.plugin.init();
        } catch (Exception e) {
            //exception = e;
            e.printStackTrace();
        }
    }

    public static VelocityImpactorBootstrap getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public Logger getLogger() {
        return this.plugin.getPluginLogger();
    }
}
