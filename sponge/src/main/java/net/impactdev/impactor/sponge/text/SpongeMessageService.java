/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.sponge.text;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;

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

public class SpongeMessageService implements MessageService<Component> {

	private static final Pattern MODIFIERS = Pattern.compile(":([sp]+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern TOKEN_LOCATOR = Pattern.compile("(^[^{]+)?([{][{](?<placeholder>[\\w-:]+)(\\|(?<arguments>.+))?[}][}])(.+)?");

	private static final Pattern STYLE_LOCATOR = Pattern.compile("([&][a-f0-9klmnor])");

	@Override
	public String getServiceName() {
		return "Messaging Service";
	}

	@Override
	public Component parse(@NonNull String message, PlaceholderSources sources) {
		Preconditions.checkNotNull(message, "Input must not be null");
		if(sources == null) {
			sources = PlaceholderSources.empty();
		}

		TextComponent.Builder output = Component.text();
		String reference = message;
		Style style = Style.empty();

		while(!reference.isEmpty()) {
			final List<Function<Component, Component>> modifiers = Lists.newArrayList();

			Matcher matcher = TOKEN_LOCATOR.matcher(reference);
			if(matcher.find()) {
				String placeholder = matcher.group("placeholder");
				String arguments = matcher.group("arguments");

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
				Component out = parser.isPresent() ? this.parseToken(parser.get(), sources, arguments) : Component.empty();

				if(matcher.group(1) != null) {
					String in = matcher.group(1);
					style = parseStyle(in);

					String prior = LegacyComponentSerializer.legacyAmpersand().serialize(output.build());
					Style inherit = parseStyle(prior);

					TextComponent test = Component.text()
							.style(inherit)
							.append(LegacyComponentSerializer.legacyAmpersand().deserialize(in))
							.build();

					output.append(test);
					reference = reference.replaceFirst("^[^{]+", "");
				}

				Component result = out;
				if (result.style().color() == null && result.style().decorations().values().stream().allMatch(state -> state == TextDecoration.State.NOT_SET)) {
					result = result.style(style.hoverEvent(result.hoverEvent()).clickEvent(result.clickEvent()));
				}

				if (!result.equals(Component.empty())) {
					for (Function<Component, Component> modifier : modifiers) {
						result = modifier.apply(result);
					}

					if (!result.children().isEmpty()) {
						style = this.locateNonEmptyStyle(result).orElse(style);
					}
					output.append(result);
				}


				reference = reference.replaceFirst("[{][{]([\\w-:]+)(\\|(.+))?[}][}]", "");
			} else {
				output.append(LegacyComponentSerializer.legacyAmpersand().deserialize(reference));
				break;
			}
		}

		return output.build();
	}

	private Component parseToken(PlaceholderParser parser, PlaceholderSources sources, String arguments) {
		Component result = parser.parse(PlaceholderContext.builder()
				.associatedObject(sources)
				.argumentString(arguments)
				.build()
		);

		if (result != Component.empty()) {
			return result;
		}

		for(Supplier<?> supplier : sources.suppliers()) {
			Component attempt = parser.parse(PlaceholderContext.builder().associatedObject(supplier).argumentString(arguments).build());
			if(attempt != Component.empty()) {
				return attempt;
			}
		}

		return Component.empty();
	}

	private Optional<PlaceholderParser> getParser(String key) {
		return RegistryTypes.PLACEHOLDER_PARSER.get()
				.stream()
				.filter(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER).formatted().equals(key))
				.findAny();
	}

	private static final BiMap<Character, TextColor> ID_TO_COLOR =
			HashBiMap.create(
					ImmutableMap.<Character, TextColor>builder()
							.put('0', NamedTextColor.BLACK)
							.put('1', NamedTextColor.DARK_BLUE)
							.put('2', NamedTextColor.DARK_GREEN)
							.put('3', NamedTextColor.DARK_AQUA)
							.put('4', NamedTextColor.DARK_RED)
							.put('5', NamedTextColor.DARK_PURPLE)
							.put('6', NamedTextColor.GOLD)
							.put('7', NamedTextColor.GRAY)
							.put('8', NamedTextColor.DARK_GRAY)
							.put('9', NamedTextColor.BLUE)
							.put('a', NamedTextColor.GREEN)
							.put('b', NamedTextColor.AQUA)
							.put('c', NamedTextColor.RED)
							.put('d', NamedTextColor.LIGHT_PURPLE)
							.put('e', NamedTextColor.YELLOW)
							.put('f', NamedTextColor.WHITE)
							.build()
			);
	private static final BiMap<Character, Style> ID_TO_STYLE =
			HashBiMap.create(
					ImmutableMap.<Character, Style>builder()
							.put('l', Style.style(TextDecoration.BOLD))
							.put('o', Style.style(TextDecoration.ITALIC))
							.put('n', Style.style(TextDecoration.UNDERLINED))
							.put('m', Style.style(TextDecoration.STRIKETHROUGH))
							.put('k', Style.style(TextDecoration.OBFUSCATED))
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

	private Optional<Style> locateNonEmptyStyle(Component component) {
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
