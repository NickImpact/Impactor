package com.nickimpact.impactor.api.commands;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.configuration.ConfigKeys;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.List;

/**
 * This class will represent a command built off the Sponge Command API, and will
 * serve as the basis for commands as they are written for a cleaner and much easier
 * registration process.
 *
 * @author NickImpact
 */
public abstract class SpongeCommand implements CommandExecutor
{
	private String basePermission;

	protected final SpongePlugin plugin;

	public SpongeCommand(SpongePlugin plugin)
	{
		this.plugin = plugin;
		if(!hasNeededAnnotations())
		{
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.ERROR, Lists.newArrayList(
					Text.of("======= Invalid Command Structure ======="),
					Text.of("Executor: ", TextColors.RED, this.getClass().getSimpleName()),
					Text.of("Reason: ", TextColors.RED, "Missing header annotation"),
					Text.of("=========================================")
			));
		}
	}

	public boolean hasNeededAnnotations()
	{
		return this.getClass().isAnnotationPresent(Aliases.class);
	}

	public List<String> getAllAliases()
	{
		return Lists.newArrayList(this.getClass().getAnnotation(Aliases.class).value());
	}

	public abstract CommandElement[] getArgs();

	public abstract Text getDescription();

	public abstract Text getUsage();

	public abstract SpongeCommand[] getSubCommands();

	private CommandSpec getCommandSpec()
	{
		this.basePermission = buildPermission(plugin);

		SpongeCommand[] subCmds = getSubCommands();
		HashMap<List<String>, CommandSpec> subCommands = new HashMap<>();
		if (subCmds != null && subCmds.length > 0)
			for (SpongeCommand cmd : subCmds)
			{
				subCommands.put(cmd.getAllAliases(), cmd.getCommandSpec());
			}

		CommandElement[] args = getArgs();
		if (args == null || args.length == 0)
			args = new CommandElement[]{GenericArguments.none()};

		if(ImpactorCore.getInstance().getConfig().get(ConfigKeys.DEBUG_ENABLED) && ImpactorCore.getInstance().getConfig().get(ConfigKeys.DEBUG_COMMANDS)) {
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
					Text.of("Command Registered: ", TextColors.DARK_AQUA, this.getAllAliases().get(0)),
					Text.of("  Aliases: ", TextColors.DARK_AQUA, this.getAllAliases()),
					Text.of("  Permission: ", TextColors.DARK_AQUA, this.basePermission)
			));
		}

		return CommandSpec.builder()
				.children(subCommands)
				.permission(this.basePermission)
				.description(getDescription())
				.executor(this)
				.arguments(args)
				.build();
	}

	public void register(SpongePlugin plugin)
	{
		try
		{
			if(this.hasNeededAnnotations())
				Sponge.getCommandManager().register(plugin, getCommandSpec(), getAllAliases());
			else
				plugin.getLogger().send(Logger.Prefixes.ERROR, Lists.newArrayList(
						Text.of(this.getClass().getSimpleName(), " has been restricted from registration")
				));
		}
		catch (IllegalArgumentException iae)
		{
			iae.printStackTrace();
		}
	}

	private String buildPermission(SpongePlugin plugin) {
		String permission = plugin.getPluginInfo().getID() + ".command.";
		if(this.getClass().isAnnotationPresent(Permission.class)) {
			Permission p = this.getClass().getAnnotation(Permission.class);

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
			} else if(p.admin()) {
				permission += "admin";
			} else {
				permission += "base";
			}
		} else {
			permission += this.getAllAliases().get(0).toLowerCase() + ".base";
		}

		return permission;
	}
}
