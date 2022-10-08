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

package net.impactdev.impactor.game.test.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandRegistrationEvent;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.PermissionsService;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.game.commands.event.ImpactorCommandRegistrationEvent;
import net.impactdev.impactor.game.commands.registration.CommandManager;
import net.impactdev.impactor.game.test.commands.permissions.AlwaysTrueOrFalsePermissionService;
import net.kyori.event.EventBus;
import net.minecraft.commands.CommandSourceStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTest {

    private static final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

    @BeforeAll
    public static void initialize() {
        CommandManager registrar = new CommandManager();
        EventBus<ImpactorEvent> events = Impactor.instance().events();

        events.subscribe(CommandRegistrationEvent.class, event -> {
            ClassGraph graph = new ClassGraph().acceptPackages("net.impactdev.impactor.game.test").enableClassInfo();
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
        assertThrows(RuntimeException.class, () -> dispatcher.execute("exceptional failing", null));
        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("exceptional passing", null)));
        assertThrows(RuntimeException.class, () -> dispatcher.execute("exceptional passing failing", null));
    }

    @Test
    public void redirection() {
        String[] usages = dispatcher.getAllUsage(dispatcher.getRoot(), null, false);
        for(String usage : usages) {
            System.out.println(usage);
        }

        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("redirecting normal", null)));
        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("redirecting n", null)));
        assertEquals(2, assertDoesNotThrow(() -> dispatcher.execute("redirecting normal child", null)));
        assertEquals(2, assertDoesNotThrow(() -> dispatcher.execute("redirecting n child", null)));
    }

    @Test
    public void requirements() {
        AlwaysTrueOrFalsePermissionService service = new AlwaysTrueOrFalsePermissionService(true);
        Impactor.instance().services().register(PermissionsService.class, service);

        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("requirements permissions", null)));

        service = new AlwaysTrueOrFalsePermissionService(false);
        Impactor.instance().services().register(PermissionsService.class, service);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("requirements permissions", null));
        CommandSyntaxException exception = assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("requirements permissions-execution-check", null));
        assertEquals("You don't have permission to execute this command!", exception.getMessage());
    }
}
