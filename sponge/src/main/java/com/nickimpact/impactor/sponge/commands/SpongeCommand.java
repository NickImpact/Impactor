package com.nickimpact.impactor.sponge.commands;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.Command;
import com.nickimpact.impactor.api.commands.annotations.Admin;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;

public abstract class SpongeCommand implements Command<SpongeCommand, CommandSpec, CommandElement, Text>, CommandExecutor {

	private final SpongeImpactorPlugin plugin;

	private String permission;

	public SpongeCommand() {
		this.plugin = SpongeImpactorPlugin.getInstance();
	}

	@Override
	public CommandSpec getCommandSpec(String current) {
		this.permission = buildPermission();
		if (current == null) {
			current = "/" + this.getAllAliases().get(0);
		} else {
			current += " " + this.getAllAliases().get(0);
		}

		SpongeCommand[] children = getSubCommands();

		HashMap<List<String>, CommandSpec> subCommands = new HashMap<>();
		if (children != null && children.length > 0)
			for (SpongeCommand cmd : children) {
				subCommands.put(cmd.getAllAliases(), cmd.getCommandSpec(current));
			}

		CommandElement[] args = getArgs();
		if (args == null || args.length == 0)
			args = new CommandElement[]{GenericArguments.none()};

		return CommandSpec.builder()
				.children(subCommands)
				.permission(this.permission)
				.description(getDescription())
				.executor(this)
				.arguments(args)
				.build();
	}

	@Override
	public String buildPermission() {
		String permission = plugin.getPluginInfo().getID() + ".command.";
		if (this.getClass().isAnnotationPresent(Permission.class)) {
			Permission p = this.getClass().getAnnotation(Permission.class);

			if (this.getClass().isAnnotationPresent(Admin.class)) {
				permission += "admin.";
			}

			if (!p.prefix().equals("")) {
				permission += p.prefix() + ".";
			}

			if (!p.value().equals("")) {
				permission += p.value();
			} else {
				permission += this.getAllAliases().get(0).toLowerCase();
			}

			permission += ".";
			if (!p.suffix().equals("")) {
				permission += p.suffix();
			} else {
				permission += "base";
			}
		} else {
			permission += this.getAllAliases().get(0).toLowerCase() + ".base";
		}

		return permission;
	}

	@Override
	public void register() {
		try {
			if (this.hasRequiredAnnotations())
				Sponge.getCommandManager().register(plugin, getCommandSpec(null), getAllAliases());
			else
				plugin.getPluginLogger().info(Lists.newArrayList(
						this.getClass().getSimpleName(), " has been restricted from registration"
				));
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}
	}
}
