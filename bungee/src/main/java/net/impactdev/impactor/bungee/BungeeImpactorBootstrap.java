package net.impactdev.impactor.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeImpactorBootstrap extends Plugin {

    private final BungeeImpactorPlugin plugin;

    private Throwable exception;

    public BungeeImpactorBootstrap() {
        this.plugin = new BungeeImpactorPlugin(this, this.getLogger());
    }

    @Override
    public void onLoad() {
        try {
            this.plugin.onLoad();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            this.plugin.onEnable();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }
    }

}
