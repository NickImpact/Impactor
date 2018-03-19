package com.nickimpact.impactor.api.commands.elements;

import com.nickimpact.impactor.time.Time;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class TimeElement extends BaseCommandElement<Time> {

	protected TimeElement(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Time parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		return null;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return null;
	}
}
