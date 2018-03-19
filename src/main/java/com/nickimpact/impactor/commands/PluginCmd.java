package com.nickimpact.impactor.commands;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.messaging.Coreination;
import com.nickimpact.impactor.api.plugins.PluginRegistry;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Tristate;

import java.util.List;

/**
 * This core command serves as a method to show off the plugins registered through
 * the core. Along with this, we should be able to enable and disable a plugin through
 * this command via click actions.
 *
 * @author NickImpact
 */
@Aliases({"plugins"})
@Permission(admin = true)
public class PluginCmd extends SpongeCommand
{
	public PluginCmd(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs()
	{
		return new CommandElement[]{
				GenericArguments.optional(GenericArguments.bool(Text.of("option")))
		};
	}

	@Override
	public Text getDescription()
	{
		return Text.of("Displays the registered plugins through the core, along with their status");
	}

	@Override
	public Text getUsage() {
		return Text.of("/plugins");
	}

	@Override
	public SpongeCommand[] getSubCommands()
	{
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
	{
		pluginMessage(src, args);

		return CommandResult.success();
	}

	private void pluginMessage(CommandSource src, CommandContext args)
	{
		Coreination.Builder coreination = Coreination.builder()
				.title(Text.of(TextColors.GREEN, TextStyles.BOLD, "Core Plugins"));

		List<SpongePlugin> plugins = PluginRegistry.getPlugins();
		List<Text> statuses = Lists.newArrayList();

		Tristate state = Tristate.UNDEFINED;
		if(args.hasAny(Text.of("option")))
			state = Tristate.fromBoolean(args.<Boolean>getOne(Text.of("option")).get());

		if(state.equals(Tristate.FALSE))
			statuses.add(Text.of(TextColors.RED, "  Disabled Plugins"));
		else if(state.equals(Tristate.TRUE))
			statuses.add(Text.of(TextColors.GREEN, "  Connected Plugins"));

		for(SpongePlugin plugin : plugins)
		{
			if(state.equals(Tristate.TRUE))
			{
				if (plugin.isConnected())
					statuses.add(pluginToText(plugin, src, args));
			}
			else if(state.equals(Tristate.FALSE))
			{
				if (!plugin.isConnected())
					statuses.add(pluginToText(plugin, src, args));
			}
			else
				statuses.add(pluginToText(plugin, src, args));
		}

		src.sendMessage(coreination.info(statuses, false).build().output());
	}

	/**
	 * This method will create a Text representation showing off a specific plugin,
	 * with extensions to enable, disable, or reload a plugin right from the chat
	 * interface.
	 *
	 * @param plugin The plugin we are displaying
	 * @return A text representation of the plugin
	 */
	private Text pluginToText(SpongePlugin plugin, CommandSource src, CommandContext args)
	{
		boolean connected = plugin.isConnected();
		Text append;
		Text text = Text.builder()
				.append(connected ? Text.of(TextColors.RED, "Disconnect") : Text.of(TextColors.GREEN, "Connect"))
				.onHover(TextActions.showText(Text.of(
						TextColors.GRAY, "Click here to change the plugin's state"
				)))
				.onClick(TextActions.executeCallback(click -> {
					if(plugin.isConnected()) {
						plugin.disconnect();
					}
					else
					{
						plugin.connect();
					}

					pluginMessage(src, args);
				}))
				.build();
		append = Text.of(plugin.isConnected() ? Text.of(TextColors.GREEN, "\u2714") : Text.of(TextColors.RED, "\u2717"),
				TextColors.GRAY, " [", text, TextColors.GRAY, "]");

		return Text.of("    ", TextColors.YELLOW, plugin.getPluginInfo().getName(), TextColors.GRAY, " \u00bb ", append);
	}
}
