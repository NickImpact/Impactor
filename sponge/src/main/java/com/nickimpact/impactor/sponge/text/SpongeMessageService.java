/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package com.nickimpact.impactor.sponge.text;

import com.google.common.base.Preconditions;
import com.nickimpact.impactor.api.services.text.MessageService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpongeMessageService implements MessageService<Text> {

	private static final Pattern TOKEN_LOCATOR = Pattern.compile("(^[^{]+)?([{][{][\\w-:]+[}][}])(.+)?");

	@Override
	public Text parse(@NonNull String message, @NonNull List<Supplier<Object>> associations) {
		Preconditions.checkNotNull(message, "Input must not be null");
		Preconditions.checkNotNull(associations, "Associations must not be null");

		Text text = Text.EMPTY;

		String reference = message;
		while(!reference.isEmpty()) {
			Matcher matcher = TOKEN_LOCATOR.matcher(reference);
			if(matcher.find()) {
				String[] token = matcher.group(2).replace("{{", "").replace("}}", "").split("\\|");

				String placeholder = token[0];
				String arguments = null;
				if(token.length > 1) {
					arguments = token[1];
				}

				Optional<PlaceholderParser> parser = this.getParser(placeholder);
				TextRepresentable out = parser.isPresent() ? this.parseToken(parser.get(), associations, arguments) : Text.EMPTY;

				if(matcher.group(1) != null) {
					text = Text.of(text, TextSerializers.FORMATTING_CODE.deserialize(matcher.group(1) + TextSerializers.FORMATTING_CODE.serialize(out.toText())));
					reference = reference.replaceFirst("^[^{]+", "");
				} else {
					text = Text.of(text, out.toText());
				}

				reference = reference.replaceFirst("[{][{][\\w-:]+[}][}]", "");
			} else {
				text = Text.of(text, TextSerializers.FORMATTING_CODE.deserialize(reference));
				break;
			}
		}

		return text;
	}

	private TextRepresentable parseToken(PlaceholderParser parser, List<Supplier<Object>> associations, String arguments) {
		for(Supplier<Object> association : associations) {
			Text result = parser.parse(PlaceholderContext.builder()
					.setAssociatedObject(association)
					.setArgumentString(arguments)
					.build()
			);
			if(result != Text.EMPTY) {
				return result;
			}
		}

		return Text.EMPTY;
	}

	private Optional<PlaceholderParser> getParser(String key) {
		return Sponge.getRegistry().getType(PlaceholderParser.class, key);
	}

	@Override
	public String getServiceName() {
		return "Messaging Service";
	}
}
