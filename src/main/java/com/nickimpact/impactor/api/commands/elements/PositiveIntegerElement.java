package com.nickimpact.impactor.api.commands.elements;

import com.google.common.collect.Lists;
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
public class PositiveIntegerElement extends BaseCommandElement<Integer> {

	private final boolean allowZero;

	public PositiveIntegerElement(@Nullable Text key) {
		this(key, true);
	}

	public PositiveIntegerElement(@Nullable Text key, boolean allowZero) {
		super(key);
		this.allowZero = allowZero;
	}

	@Nullable
	@Override
	protected Integer parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		try {
			int a = Integer.parseUnsignedInt(args.next());
			if (allowZero || a != 0) {
				return a;
			}

			throw args.createError(Text.of("The argument cannot be equal to 0"));
		} catch (NumberFormatException e) {
			throw args.createError(Text.of("The argument must be a positive number"));
		}
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return Lists.newArrayList();
	}
}
