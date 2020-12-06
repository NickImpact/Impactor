/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package net.impactdev.impactor.sponge.text;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.kyori.text.TextComponent;
import net.kyori.text.format.Style;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpongeMessageService implements MessageService<Text> {

	private static final Pattern MODIFIERS = Pattern.compile(":([sp]+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern TOKEN_LOCATOR = Pattern.compile("(^[^{]+)?([{][{][\\w-:]+[}][}])(.+)?");

	private static final Pattern STYLE_LOCATOR = Pattern.compile("([&][a-f0-9klmnor])");

	@Override
	public String getServiceName() {
		return "Messaging Service";
	}

	@Override
	public Text parse(@NonNull String message, @NonNull List<Supplier<Object>> associations) {
		Preconditions.checkNotNull(message, "Input must not be null");
		Preconditions.checkNotNull(associations, "Associations must not be null");

		TextComponent.Builder output = TextComponent.builder();
		final List<Function<TextComponent, TextComponent>> modifiers = Lists.newArrayList();

		String reference = message;

		Style style = Style.empty();
		while(!reference.isEmpty()) {
			Matcher matcher = TOKEN_LOCATOR.matcher(reference);
			if(matcher.find()) {
				String[] token = matcher.group(2).replace("{{", "").replace("}}", "").split("\\|");

				String placeholder = token[0];
				String arguments = null;
				if(token.length > 1) {
					arguments = token[1];
				}

				final Matcher m = MODIFIERS.matcher(placeholder);
				if(m.find()) {
					String match = m.group(1).toLowerCase();
					for (TextModifiers modifier : TextModifiers.values()) {
						if(match.contains(modifier.getKey())) {
							modifiers.add(modifier);
						}
					}

					placeholder = placeholder.replaceAll(MODIFIERS.pattern(), "");
				}

				Optional<PlaceholderParser> parser = this.getParser(placeholder);
				TextRepresentable out = parser.isPresent() ? this.parseToken(parser.get(), associations, arguments) : Text.EMPTY;

				if(matcher.group(1) != null) {
					String in = matcher.group(1);
					style = parseStyle(in);

					String prior = LegacyComponentSerializer.legacy().serialize(output.build(), '&');
					Style inherit = parseStyle(prior);

					output.append(LegacyComponentSerializer.legacy().deserialize(in, '&').style(inherit));
					reference = reference.replaceFirst("^[^{]+", "");
				}

				TextComponent result = ((TextComponent) GsonComponentSerializer.INSTANCE.deserialize(TextSerializers.JSON.serialize(out.toText())));
				if(result.style().color() == null && result.style().decorations().isEmpty()) {
					result = result.style(style.hoverEvent(result.hoverEvent()).clickEvent(result.clickEvent()));
				}

				if(!result.isEmpty()) {
					for (Function<TextComponent, TextComponent> modifier : modifiers) {
						result = modifier.apply(result);
					}

					if(!result.children().isEmpty()) {
						style = this.locateNonEmptyStyle(result).orElse(style);
					}
					output.append(result);
				}

				reference = reference.replaceFirst("[{][{][\\w-:]+[}][}]", "");
			} else {
				output.append(LegacyComponentSerializer.legacy().deserialize(reference, '&').style(style));
				break;
			}
		}

		return TextSerializers.JSON.deserialize(GsonComponentSerializer.INSTANCE.serialize(output.build()));
	}

	private TextRepresentable parseToken(PlaceholderParser parser, List<Supplier<Object>> associations, String arguments) {
		if(associations.isEmpty()) {
			return parser.parse(PlaceholderContext.builder()
					.setArgumentString(arguments)
					.build());
		} else {
			for (Supplier<Object> association : associations) {
				Text result = parser.parse(PlaceholderContext.builder()
						.setAssociatedObject(association)
						.setArgumentString(arguments)
						.build()
				);
				if (result != Text.EMPTY) {
					return result;
				}
			}
		}

		return Text.EMPTY;
	}

	private Optional<PlaceholderParser> getParser(String key) {
		return Sponge.getRegistry().getType(PlaceholderParser.class, key);
	}

	private static final BiMap<Character, TextColor> ID_TO_COLOR =
			HashBiMap.create(
					ImmutableMap.<Character, TextColor>builder()
							.put('0', TextColor.BLACK)
							.put('1', TextColor.DARK_BLUE)
							.put('2', TextColor.DARK_GREEN)
							.put('3', TextColor.DARK_AQUA)
							.put('4', TextColor.DARK_RED)
							.put('5', TextColor.DARK_PURPLE)
							.put('6', TextColor.GOLD)
							.put('7', TextColor.GRAY)
							.put('8', TextColor.DARK_GRAY)
							.put('9', TextColor.BLUE)
							.put('a', TextColor.GREEN)
							.put('b', TextColor.AQUA)
							.put('c', TextColor.RED)
							.put('d', TextColor.LIGHT_PURPLE)
							.put('e', TextColor.YELLOW)
							.put('f', TextColor.WHITE)
							.build()
			);
	private static final BiMap<Character, Style> ID_TO_STYLE =
			HashBiMap.create(
					ImmutableMap.<Character, Style>builder()
							.put('l', Style.of(TextDecoration.BOLD))
							.put('o', Style.of(TextDecoration.ITALIC))
							.put('n', Style.of(TextDecoration.UNDERLINED))
							.put('m', Style.of(TextDecoration.STRIKETHROUGH))
							.put('k', Style.of(TextDecoration.OBFUSCATED))
							.put('r', Style.empty())
							.build()
			);

	private TextColor getColor(String style) {
		Pattern pattern = Pattern.compile("[a-f0-9]");
		Matcher matcher = pattern.matcher(style);

		TextColor color = null;
		while(matcher.find()) {
			color = ID_TO_COLOR.get(matcher.group().charAt(0));
		}

		return color;
	}

	private Style parseStyle(String in) {
		Queue<String> queue = EvictingQueue.create(2);
		Matcher matcher = STYLE_LOCATOR.matcher(in);
		while(matcher.find()) {
			queue.add(matcher.group(1));
		}

		StringJoiner joiner = new StringJoiner("");
		for(String s : queue) {
			joiner.add(s);
		}

		return this.getStyle(joiner.toString());
	}

	private Style getStyle(String style) {
		Style result = Style.empty();
		TextColor color = this.getColor(style);
		if(color != null) {
			result = result.color(color);
		}

		Pattern pattern = Pattern.compile("[k-or]");
		Matcher matcher = pattern.matcher(style);
		while(matcher.find()) {
			result = result.merge(ID_TO_STYLE.get(matcher.group().charAt(0)));
		}

		return result;
	}

	private Optional<Style> locateNonEmptyStyle(TextComponent component) {
		List<TextComponent> children = component.children().stream()
				.filter(c -> c instanceof TextComponent)
				.map(c -> (TextComponent) c)
				.collect(Collectors.toList());

		Collections.reverse(children);

		for(TextComponent child : children) {
			if(!child.style().equals(Style.empty())) {
				return Optional.of(child.style());
			}

			if(!child.children().isEmpty()) {
				return this.locateNonEmptyStyle(child);
			}
		}

		return Optional.empty();
	}

}
