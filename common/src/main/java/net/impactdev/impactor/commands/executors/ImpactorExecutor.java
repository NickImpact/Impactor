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

package net.impactdev.impactor.commands.executors;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.permissions.PermissionsService;
import net.impactdev.impactor.api.commands.annotations.permissions.Permission;
import net.impactdev.impactor.api.commands.annotations.permissions.Phase;
import net.impactdev.impactor.api.commands.exceptions.CommandResultException;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.commands.executors.CommandSource;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ImpactorExecutor<S> implements Command<S> {

    private final CommandExecutor delegate;
    private final @Nullable Permission permission;

    public ImpactorExecutor(CommandExecutor executor, @Nullable Permission permission) {
        this.delegate = executor;
        this.permission = permission;
    }

    @Override
    public int run(com.mojang.brigadier.context.CommandContext<S> context) throws CommandSyntaxException {
        try {
            CommandContext impactor = new ImpactorCommandContext<>(context);
            if(this.verify(impactor.source())) {
                CommandResult result = this.delegate.execute(impactor);
                if (result.reason().isPresent()) {
                    throw new CommandResultException(result, result.reason().get());
                }

                return result.result();
            } else {
                throw new NoPermissionExceptionType().create();
            }
        } catch (CommandResultException tracked) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Command Parsing Exception");
            printer.add("A tracked exception occurred while attempting to process")
                    .add("a command! This typically indicates the exception was expected")
                    .add("and was logged by the result parser. Information about the error")
                    .add("is listed below...");

            printer.hr('-');
            printer.add("Raw Input: " + context.getInput());
            printer.add("Result:");
            printer.add("Successful: " + tracked.result().isSuccessful(), 2);
            printer.add("Result Value: " + tracked.result().result(), 2);
            printer.newline();
            printer.add("Parse Results:");
            printer.add(context.getInput()).add(StringUtils.leftPad("^", context.getRange().getEnd()));

            printer.hr('-').add("Tracked Exception Stacktrace:");
            printer.add(tracked.getCause());

            printer.print(System.err);
            throw new RuntimeException("Tracked Exception", tracked.getCause());
        } catch (RuntimeException unexpected) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Command Parsing Exception");
            printer.add("An unexpected exception occurred while attempting to process");
            printer.add("a command!");
            printer.hr('-');
            printer.add("Raw Input: " + context.getInput());
            printer.newline();
            printer.add("Parse Results:");
            printer.add(context.getInput()).add(StringUtils.leftPad("^", context.getRange().getEnd()));
            printer.hr('-').add("Exception Stacktrace:");
            printer.add(unexpected);

            printer.print(System.err);
            throw unexpected;
        }
    }

    private boolean verify(CommandSource source) {
        return Optional.ofNullable(this.permission)
                .filter(p -> p.phase().equals(Phase.EXECUTION))
                .map(p -> Impactor.instance().services()
                        .provide(PermissionsService.class)
                        .hasPermission(
                                Optional.ofNullable(source).map(CommandSource::asPlatform).orElse(null),
                                p.value()
                        )
                )
                .orElse(true);
    }

    private static class NoPermissionExceptionType extends SimpleCommandExceptionType {

        public NoPermissionExceptionType() {
            super(() -> "You don't have permission to execute this command!");
        }

    }

}
