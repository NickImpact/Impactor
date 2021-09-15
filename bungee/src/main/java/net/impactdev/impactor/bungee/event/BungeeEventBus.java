package net.impactdev.impactor.bungee.event;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.event.EventSubscription;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.common.event.AbstractEventBus;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BungeeEventBus extends AbstractEventBus<Plugin> implements Listener {

    private final Plugin bootstrap;

    public BungeeEventBus(Plugin plugin) {
        this.bootstrap = plugin;
    }

    @Override
    protected Plugin checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof Plugin) {
            Plugin bungeePlugin = (Plugin) plugin;

            // add a custom log handler to effectively listen for the plugin being disabled.
            // BungeeCord doesn't really support enabling/disabling plugins at runtime, and as
            // such doesn't have a PluginDisableEvent. However, some plugins do exist to reload
            // plugins at runtime. We rely on these plugins following the BungeeCord behaviour,
            // and #close ing the plugins logger, so we can unregister the listeners. :)
            Handler[] handlers = bungeePlugin.getLogger().getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof UnloadHookLoggerHandler) {
                    return bungeePlugin;
                }
            }

            bungeePlugin.getLogger().addHandler(new UnloadHookLoggerHandler(bungeePlugin));
            return bungeePlugin;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

    @Override
    public void close() {
        for (Plugin plugin : this.bootstrap.getProxy().getPluginManager().getPlugins()) {
            for (Handler handler : plugin.getLogger().getHandlers()) {
                if (handler instanceof UnloadHookLoggerHandler) {
                    plugin.getLogger().removeHandler(handler);
                }
            }
        }

        super.close();
    }

    private final class UnloadHookLoggerHandler extends Handler {
        private final Plugin plugin;

        private UnloadHookLoggerHandler(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void close() {
            unregisterHandlers(this.plugin);
        }

        @Override public void publish(LogRecord record) {}
        @Override public void flush() {}
    }
}
