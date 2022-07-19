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
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.services.parsing.StringParser;
import net.impactdev.impactor.api.services.parsing.PlaceholderParser;
import net.impactdev.impactor.api.services.parsing.GenericTextParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpongeMessageService implements MessageService {

	private static final Pattern TOKEN_LOCATOR = Pattern.compile("(^[^{]+)?(?<raw>[{][{](?<placeholder>[\\w-:]+)(\\|(?<arguments>.+))?[}][}])(.+)?");

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

		Stack<Component> components = new Stack<>();

		String reference = message;
		while(!reference.isEmpty()) {
			Matcher matcher = TOKEN_LOCATOR.matcher(reference);
			if(matcher.find()) {
				String placeholder = matcher.group("placeholder");
				String arguments = matcher.group("arguments");

				if(matcher.group(1) != null) {
					GenericTextParser generic = new GenericTextParser(matcher.group(1));
					components.addAll(generic.components());
					reference = reference.replaceFirst("^[^{]+", "");
				}

				PlaceholderParser parser = new SpongePlaceholderParser(matcher.group("raw"), placeholder, arguments, sources);
				components.addAll(parser.components());

				reference = reference.replaceFirst("[{][{]([\\w-:]+)(\\|(.+))?[}][}]", "");
			} else {
				GenericTextParser generic = new GenericTextParser(reference);
				components.addAll(generic.components());
				break;
			}
		}

		Component result = null;
		while(!components.empty()) {
			Component component = components.pop();
			if(result == null) {
				result = component;
			} else {
				result = component.append(result);
			}
		}

		return result == null ? Component.empty() : result;

	}
}
