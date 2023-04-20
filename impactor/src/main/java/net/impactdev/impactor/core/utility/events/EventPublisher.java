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

package net.impactdev.impactor.core.utility.events;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.event.PostResult;

import java.util.concurrent.atomic.AtomicInteger;

public final class EventPublisher {

    public static void post(ImpactorEvent event) {
        PostResult result = Impactor.instance().events().post(event);

        try {
            result.raise();
        } catch (PostResult.CompositeException exception) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Event Subscriber Exceptions")
                    .consume(p -> {
                        int tracked = exception.result().exceptions().size();
                        p.add("Exceptions were encountered while processing event subscribers to")
                            .add("the given event type: " + event.getClass().getSimpleName())
                            .newline()
                            .add("These errors will now be listed below...");
                    })
                    .hr('-')
                    .consume(p -> {
                        p.newline();
                        AtomicInteger index = new AtomicInteger(1);
                        exception.result().exceptions()
                                .forEach((subscriber, error) -> {
                                    p.add("%d: %s", index.getAndIncrement(), subscriber);
                                    p.add(error, 2);
                                });
                        p.newline();
                    });
            printer.log(BaseImpactorPlugin.instance().logger());
        }
    }

}
