/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.common.event.gen;

import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.event.annotations.Param;
import net.impactdev.impactor.api.utilities.mappings.Tuple;
import net.impactdev.impactor.api.utilities.mappings.LoadingMap;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Holds the generated event class for a given type of {@link ImpactorEvent}.
 */
public class GeneratedEventClass {

    /**
     * A loading cache of event types to {@link GeneratedEventClass}es.
     */
    private static final Map<Tuple<Class<? extends ImpactorEvent>, Type>, GeneratedEventClass> CACHE = LoadingMap.of(clazz -> {
        try {
            return new GeneratedEventClass(clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Generate a {@link GeneratedEventClass} for the given {@code event} type.
     *
     * @param event the event type
     * @return the generated class
     */
    public static GeneratedEventClass generate(Tuple<Class<? extends ImpactorEvent>, Type> event) {
        return CACHE.get(event);
    }

    /**
     * A method handle for the constructor of the event class.
     */
    private final MethodHandle constructor;

    /**
     * An array of {@link MethodHandle}s, which can set values for each of the properties in the event class.
     */
    private final MethodHandle[] setters;

    private GeneratedEventClass(Tuple<Class<? extends ImpactorEvent>, Type> tuple) throws Throwable {
        Class<? extends ImpactorEvent> event = tuple.getFirst();
        @Nullable Type generic = tuple.getSecond();

        // get a TypeDescription for the event class
        TypeDescription eventClassType = new TypeDescription.ForLoadedType(event);
        String name = event.getName() + (generic != null ? "$" + this.shorten(generic.getTypeName()) : "") + "$Impl";

        DynamicType.Builder<AbstractEvent> builder = new ByteBuddy(ClassFileVersion.JAVA_V8)
                // create a subclass of AbstractEvent
                .subclass(AbstractEvent.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                // using the predetermined generated class name
                .name(name)
                // implement the event interface
                .implement(eventClassType)
                // implement all methods annotated with Param by simply returning the value from the corresponding field with the same name
                .method(isAnnotatedWith(Param.class))
                .intercept(FieldAccessor.of(NamedElement.WithRuntimeName::getInternalName))
                // implement ImpactorEvent#getEventType by returning the event class type
                .method(named("getEventType").and(returns(Class.class)).and(takesArguments(0)))
                .intercept(FixedValue.value(eventClassType))
                // implement AbstractEvent#mhl by calling & returning the value of MethodHandles.lookup()
                .method(named("mhl").and(returns(MethodHandles.Lookup.class)).and(takesArguments(0)))
                .intercept(MethodCall.invoke(MethodHandles.class.getMethod("lookup")))
                // implement a toString method
                .withToString();

        // get a sorted array of all methods on the event interface annotated with @Param
        Method[] properties = Arrays.stream(event.getMethods())
                .filter(m -> m.isAnnotationPresent(Param.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Param.class).value()))
                .toArray(Method[]::new);

        // for each property, define a field on the generated class to hold the value
        for (Method method : properties) {
            builder = builder.defineField(method.getName(), method.getReturnType(), Visibility.PRIVATE);
        }

        // finish building, load the class, get a constructor
        Class<? extends AbstractEvent> generatedClass = builder.make().load(GeneratedEventClass.class.getClassLoader()).getLoaded();
        this.constructor = MethodHandles.publicLookup().in(generatedClass)
                .findConstructor(generatedClass, MethodType.methodType(void.class))
                .asType(MethodType.methodType(AbstractEvent.class));

        // create a dummy instance of the generated class & get the method handle lookup instance
        MethodHandles.Lookup lookup = ((AbstractEvent) this.constructor.invoke()).mhl();

        // get 'setter' MethodHandles for each property
        this.setters = new MethodHandle[properties.length];
        for (int i = 0; i < properties.length; i++) {
            Method method = properties[i];
            this.setters[i] = lookup.findSetter(generatedClass, method.getName(), method.getReturnType())
                    .asType(MethodType.methodType(void.class, new Class[]{AbstractEvent.class, Object.class}));
        }
    }

    /**
     * Creates a new instance of the event class.
     *
     * @param properties the event properties
     * @return the event instance
     * @throws Throwable if something goes wrong
     */
    public ImpactorEvent newInstance(List<Object> properties) throws Throwable {
        if (properties.size() != this.setters.length) {
            throw new IllegalStateException("Unexpected number of properties. given: " + properties.size() + ", expected: " + this.setters.length);
        }

        // create a new instance of the event
        final AbstractEvent event = (AbstractEvent) this.constructor.invokeExact();

        // set the properties onto the event instance
        for (int i = 0; i < this.setters.length; i++) {
            MethodHandle setter = this.setters[i];
            Object value = properties.get(i);
            setter.invokeExact(event, value);
        }

        return event;
    }

    private static final Pattern FILTER = Pattern.compile("^([a-zA-Z]+[.])+([a-zA-Z$]+)(<([a-zA-Z]+[.])+([a-zA-Z$]+)>)?");

    private String shorten(String type) {
        Matcher matcher = FILTER.matcher(type);
        if(matcher.find()) {
            StringJoiner joiner = new StringJoiner("_");
            joiner.add(matcher.group(2));
            if(matcher.group(5) != null) {
                joiner.add(matcher.group(5));
            }
            return joiner.toString();
        } else {
            throw new RuntimeException("Failed decoding a shortened typing...");
        }
    }
}
