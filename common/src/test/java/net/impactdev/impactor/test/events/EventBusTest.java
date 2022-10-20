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

package net.impactdev.impactor.test.events;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.kyori.event.AbstractCancellable;
import net.kyori.event.EventBus;
import net.kyori.event.EventSubscription;
import net.kyori.event.PostResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class EventBusTest {

    @Test
    public void basic() {
        EventBus<ImpactorEvent> bus = Impactor.instance().events();
        AtomicInteger capture = new AtomicInteger(-1);

        EventSubscription subscription = bus.subscribe(TestEvent.class, event -> {
            capture.set(event.value());
        });
        PostResult result = bus.post(new TestEvent(10));
        Assertions.assertTrue(result.wasSuccessful());
        Assertions.assertEquals(10, capture.get());

        result = bus.post(new TestEvent(17));
        Assertions.assertTrue(result.wasSuccessful());
        Assertions.assertEquals(17, capture.get());
        subscription.unsubscribe();
    }

    private static final class TestEvent extends AbstractCancellable implements ImpactorEvent {

        private final int value;

        public TestEvent(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }

}
