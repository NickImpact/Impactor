/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.nickimpact.impactor.common.event.gen;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.nickimpact.impactor.api.event.ImpactorEvent;
import com.nickimpact.impactor.api.event.type.Cancellable;
import com.nickimpact.impactor.api.utilities.Tuple;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EventGenerator {

    public <T extends ImpactorEvent> T generate(Class<T> event, Object... params) {
        return this.generate(event, null, params);
    }

    @SuppressWarnings("unchecked")
    public <T extends ImpactorEvent> T generate(Class<T> event, Type generic, Object... params) {
        try {
            List<Object> parameters = Lists.newArrayList(params);
            if(Cancellable.class.isAssignableFrom(event)) {
                parameters.add(0, new AtomicBoolean(false));
            }

            if(generic != null) {
                parameters.add(0, TypeToken.get(generic));
            }

            return (T) GeneratedEventClass.generate(new Tuple<>(event, generic)).newInstance(parameters);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to generate event instance", e);
        }
    }

}
