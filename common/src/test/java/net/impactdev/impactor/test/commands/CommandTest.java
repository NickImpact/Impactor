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

package net.impactdev.impactor.test.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandRegistrationEvent;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.PermissionsService;
import net.impactdev.impactor.api.commands.executors.CommandSource;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.platform.players.PlatformSource;
import net.impactdev.impactor.commands.event.ImpactorCommandRegistrationEvent;
import net.impactdev.impactor.commands.executors.ImpactorCommandSource;
import net.impactdev.impactor.commands.registration.CommandManager;
import net.impactdev.impactor.commands.sources.SourceTranslator;
import net.impactdev.impactor.test.commands.permissions.AlwaysTrueOrFalsePermissionService;
import net.kyori.event.EventBus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CommandTest {

    private static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private static final CommandSource DUMMY = new ImpactorCommandSource(mock(PlatformSource.class));

    @BeforeAll
    public static void initialize() {
        Impactor.instance().services()
                .provide(SourceTranslator.class)
                .register(ImpactorCommandSource.class, source -> DUMMY);

        CommandManager<CommandSource> registrar = new CommandManager<>();
        EventBus<ImpactorEvent> events = Impactor.instance().events();

        events.subscribe(CommandRegistrationEvent.class, event -> {
            ClassGraph graph = new ClassGraph().acceptPackages("net.impactdev.impactor.test").enableClassInfo();
            try (ScanResult scan = graph.scan()) {
                ClassInfoList list = scan.getClassesImplementing(ImpactorCommand.class);
                list.stream()
                        .map(info -> info.loadClass(ImpactorCommand.class))
                        .filter(type -> !type.isInterface())
                        .map(type -> {
                            try {
                                return type.newInstance();
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .forEach(registrar::register);
            }
        });
        events.post(new ImpactorCommandRegistrationEvent(registrar));
        registrar.registerWithBrigadier(dispatcher);
    }

    @Test
    public void exceptionTest() {
        assertThrows(RuntimeException.class, () -> dispatcher.execute("exceptional failing", DUMMY));
        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("exceptional passing", DUMMY)));
        assertThrows(RuntimeException.class, () -> dispatcher.execute("exceptional passing failing", DUMMY));
    }

    @Test
    public void redirection() {
        String[] usages = dispatcher.getAllUsage(dispatcher.getRoot(), DUMMY, false);
        for(String usage : usages) {
            System.out.println(usage);
        }

        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("redirecting normal", DUMMY)));
        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("redirecting n", DUMMY)));
        assertEquals(2, assertDoesNotThrow(() -> dispatcher.execute("redirecting normal child", DUMMY)));
        assertEquals(2, assertDoesNotThrow(() -> dispatcher.execute("redirecting n child", DUMMY)));
    }

    @Test
    public void requirements() {
        AlwaysTrueOrFalsePermissionService service = new AlwaysTrueOrFalsePermissionService(true);
        Impactor.instance().services().register(PermissionsService.class, service);

        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("requirements permissions", DUMMY)));

        service = new AlwaysTrueOrFalsePermissionService(false);
        Impactor.instance().services().register(PermissionsService.class, service);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("requirements permissions", DUMMY));
        CommandSyntaxException exception = assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("requirements permissions-execution-check", DUMMY));
        assertEquals("You don't have permission to execute this command!", exception.getMessage());
    }
}
