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

package net.impactdev.impactor.api.scoreboard.effects;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class CountdownEffect implements FrameEffect {

    private final LocalDateTime target;
    private final Component complete;

    private CountdownEffect(CountdownEffectBuilder builder) {
        this.target = builder.target;
        this.complete = builder.complete;
    }

    @Override
    public String getID() {
        return "countdown";
    }

    @Override
    public Component translate(Component parent) {
        if(LocalDateTime.now().isAfter(this.target)) {
            return this.complete;
        }

        long seconds = Duration.between(LocalDateTime.now(), this.target).getSeconds();
        String result;
        if(TimeUnit.SECONDS.toDays(seconds) / 7 > 0) {
            result = (TimeUnit.SECONDS.toDays(seconds) / 7) + " weeks";
        } else if(TimeUnit.SECONDS.toDays(seconds) % 7 > 0) {
            result = (TimeUnit.SECONDS.toDays(seconds) % 7) + " days";
        } else {
            StringJoiner joiner = new StringJoiner(":");
            if(TimeUnit.SECONDS.toHours(seconds) > 0) {
                joiner.add(TimeUnit.SECONDS.toHours(seconds) + "");
            } else if(TimeUnit.SECONDS.toMinutes(seconds) > 0) {
                joiner.add((TimeUnit.SECONDS.toMinutes(seconds) % 60) + "");
            } else {
                joiner.add((seconds % 60) + "");
            }

            result = joiner.toString();
        }

        return parent.replaceText(TextReplacementConfig.builder()
                .matchLiteral("{countdown}")
                .once()
                .replacement(result)
                .build()
        );
    }

    public static CountdownEffectBuilder builder() {
        return new CountdownEffectBuilder();
    }

    public static class CountdownEffectBuilder implements Builder<CountdownEffect> {

        private LocalDateTime target;
        private Component complete;

        public CountdownEffectBuilder target(LocalDateTime target) {
            this.target = target;
            return this;
        }

        public CountdownEffectBuilder whenComplete(String raw, Context context) {
            MessageService service = Impactor.instance().services().provide(MessageService.class);

            this.complete = service.parse(raw, context);
            return this;
        }

        @Override
        public CountdownEffect build() {
            return new CountdownEffect(this);
        }
    }

}
