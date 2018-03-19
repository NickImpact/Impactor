package com.nickimpact.impactor.api.commands.elements;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public abstract class BaseCommandElement<T> extends CommandElement {

	protected BaseCommandElement(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected abstract T parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException;

	@Override
	public abstract List<String> complete(CommandSource src, CommandArgs args, CommandContext context);
}
